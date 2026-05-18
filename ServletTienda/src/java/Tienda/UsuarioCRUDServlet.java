/*
 * UsuarioCRUDServlet.java
 * Este servlet permite a los usuarios actualizar o eliminar su cuenta.
 * - En el método doGet: muestra un formulario HTML para modificar o eliminar la cuenta.
 * - En el método doPost: verifica la contraseña actual, actualiza los datos si se solicita, o elimina la cuenta.
 */

package Tienda;

import java.io.*; // Manejo de entrada/salida
import java.sql.*; // Conexión y manipulación de base de datos
import jakarta.servlet.*; // Funcionalidad general de servlets
import jakarta.servlet.http.*; // Funcionalidad específica de HTTP como sesiones y peticiones

public class UsuarioCRUDServlet extends HttpServlet {

    // Muestra el formulario HTML al usuario
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Define el tipo de contenido de la respuesta
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter(); // Permite escribir en la respuesta

        // Empieza el HTML
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Cuenta</title>");
        out.println("<style>");
        out.println("body { font-family: Arial; background-color: #f5f5f5; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }");
        out.println(".form-container { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); width: 400px; }");
        out.println("input[type=text], input[type=email], input[type=password] { width: 100%; padding: 10px; margin: 8px 0; border-radius: 5px; border: 1px solid #ccc; box-sizing: border-box; }");
        out.println("button { padding: 10px 20px; border: none; border-radius: 5px; background-color: #007bff; color: white; cursor: pointer; margin-top: 10px; }");
        out.println("button[name='action'][value='eliminar'] { background-color: #dc3545; float: right; }");
        out.println(".message { padding: 10px; margin: 15px 0; border-radius: 5px; }");
        out.println(".success { background-color: #d4edda; color: #155724; }");
        out.println(".error { background-color: #f8d7da; color: #721c24; }");
        out.println("</style></head><body>");
        out.println("<div class='form-container'>");
        out.println("<h2>Modificar o Eliminar Cuenta</h2>");

        // Mostrar mensaje de éxito o error, si existe
        String mensaje = request.getParameter("mensaje");
        if (mensaje != null) {
            String clase = "";
            String texto = "";

            switch(mensaje) {
                case "cuenta_actualizada":
                    clase = "success";
                    texto = "Cuenta actualizada exitosamente";
                    break;
                case "cuenta_eliminada":
                    clase = "success";
                    texto = "Cuenta eliminada exitosamente";
                    break;
                case "contrasena_incorrecta":
                    clase = "error";
                    texto = "La contraseña actual no es correcta";
                    break;
                case "usuario_no_encontrado":
                    clase = "error";
                    texto = "Usuario no encontrado";
                    break;
                case "error_actualizacion":
                    clase = "error";
                    texto = "Error al actualizar la cuenta";
                    break;
                case "error_eliminacion":
                    clase = "error";
                    texto = "Error al eliminar la cuenta";
                    break;
                case "error_servidor":
                    clase = "error";
                    texto = "Error en el servidor. Por favor, intente más tarde";
                    break;
                default:
                    clase = "error";
                    texto = "Error al procesar la solicitud";
            }

            out.println("<div class='message " + clase + "'>" + texto + "</div>");
        }

        // Formulario para editar o eliminar cuenta
        out.println("<form method='post' action='UsuarioCRUD'>");
        out.println("<label for='email'>Email:</label>");
        out.println("<input type='email' id='email' name='email' required>");
        out.println("<label for='nombre'>Nombre:</label>");
        out.println("<input type='text' id='nombre' name='nombre'>");
        out.println("<label for='direccion'>Dirección:</label>");
        out.println("<input type='text' id='direccion' name='direccion'>");
        out.println("<label for='contrasena_actual'>Contraseña actual:</label>");
        out.println("<input type='password' id='contrasena_actual' name='contrasena_actual' required>");
        out.println("<label for='contrasena'>Nueva contraseña (dejar vacío para mantener la actual):</label>");
        out.println("<input type='password' id='contrasena' name='contrasena'>");
        out.println("<button type='submit' name='action' value='actualizar'>Actualizar</button>");
        out.println("<button type='submit' name='action' value='eliminar'>Eliminar Cuenta</button>");
        out.println("</form>");
        out.println("</div>");
        out.println("</body></html>");
    }

    // Procesa la solicitud POST para actualizar o eliminar cuenta
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtener parámetros del formulario
        String action = request.getParameter("action");
        String email = request.getParameter("email");
        String nuevoNombre = request.getParameter("nombre");
        String nuevaDireccion = request.getParameter("direccion");
        String nuevaContrasena = request.getParameter("contrasena");
        String contrasenaActual = request.getParameter("contrasena_actual");

        try (Connection conn = ConexionBD.obtenerConexion()) {
            // Verificar si el usuario existe y si la contraseña es correcta
            PreparedStatement verifStmt = conn.prepareStatement(
                "SELECT id, contrasena FROM usuarios1 WHERE email = ?");
            verifStmt.setString(1, email);
            ResultSet rs = verifStmt.executeQuery();

            if (!rs.next()) {
                response.sendRedirect("UsuarioCRUD?mensaje=usuario_no_encontrado");
                return;
            }

            String contrasenaEnBD = rs.getString("contrasena");
            if (!contrasenaEnBD.equals(contrasenaActual)) {
                response.sendRedirect("UsuarioCRUD?mensaje=contrasena_incorrecta");
                return;
            }

            int usuarioId = rs.getInt("id");

            if ("actualizar".equals(action)) {
                // Actualizar datos del usuario
                String sql = "UPDATE usuarios1 SET nombre = ?, direccion = ?";
                if (nuevaContrasena != null && !nuevaContrasena.isEmpty()) {
                    sql += ", contrasena = ?";
                }
                sql += " WHERE id = ?";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nuevoNombre);
                stmt.setString(2, nuevaDireccion);

                int paramIndex = 3;
                if (nuevaContrasena != null && !nuevaContrasena.isEmpty()) {
                    stmt.setString(paramIndex++, nuevaContrasena);
                }
                stmt.setInt(paramIndex, usuarioId);

                int filas = stmt.executeUpdate();

                if (filas > 0) {
                    response.sendRedirect("catalogo.html?mensaje=cuenta_actualizada");
                } else {
                    response.sendRedirect("UsuarioCRUD?mensaje=error_actualizacion");
                }

            } else if ("eliminar".equals(action)) {
                // Eliminar cuenta del usuario
                PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM usuarios1 WHERE id = ?");
                stmt.setInt(1, usuarioId);
                int filas = stmt.executeUpdate();

                if (filas > 0) {
                    // Invalidar sesión si existe
                    HttpSession session = request.getSession(false);
                    if (session != null) {
                        session.invalidate();
                    }
                    response.sendRedirect("index.html?mensaje=cuenta_eliminada");
                } else {
                    response.sendRedirect("UsuarioCRUD?mensaje=error_eliminacion");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Imprime error en consola
            response.sendRedirect("UsuarioCRUD?mensaje=error_servidor");
        }
    }
}
