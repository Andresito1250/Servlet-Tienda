package Tienda; // Define el paquete al que pertenece esta clase

// Importación de clases necesarias
import java.io.*; // Para manejo de entrada/salida (PrintWriter, IOException, etc.)
import java.sql.*; // Para manejo de base de datos con JDBC
import jakarta.servlet.*; // Para uso de clases de servlet
import jakarta.servlet.http.*; // Para trabajar con peticiones y respuestas HTTP
import static java.lang.System.out; // Permite usar System.out directamente

// Clase ServletCompra que extiende HttpServlet para manejar solicitudes HTTP
public class ServletCompra extends HttpServlet {

    // Método que maneja las solicitudes POST (envíos desde formularios)
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtiene el email y el nombre del producto enviados desde el formulario
        String email = request.getParameter("email");
        String producto = request.getParameter("producto"); // Puede ser "Camisa", "Jean", o "Zapatos"

        // Define el tipo de contenido de la respuesta como HTML en UTF-8
        response.setContentType("text/html;charset=UTF-8");

        // Se abre un PrintWriter para enviar la respuesta al navegador
        try (PrintWriter out = response.getWriter();
             Connection conn = ConexionBD.obtenerConexion()) { // Se obtiene conexión a la base de datos

            // Buscar usuario en la base de datos por su email
            PreparedStatement psUser = conn.prepareStatement("SELECT id, direccion FROM usuarios1 WHERE email = ?");
            psUser.setString(1, email); // Asigna el email al parámetro de la consulta
            ResultSet rsUser = psUser.executeQuery(); // Ejecuta la consulta

            // Si el usuario no existe, se muestra un mensaje de error
            if (!rsUser.next()) {
                out.println("<div style='text-align:center; background-color:#ffebee; padding:20px; border-radius:10px;'>"
                        + "<h2 style='color:#d32f2f;'>Usuario no encontrado</h2></div>");
                return; // Sale del método sin continuar
            }

            // Se obtiene el ID y la dirección del usuario
            int usuarioId = rsUser.getInt("id");
            String direccion = rsUser.getString("direccion");

            // Buscar el producto en la base de datos por su nombre
            PreparedStatement psProd = conn.prepareStatement("SELECT id FROM productos WHERE nombre = ?");
            psProd.setString(1, producto); // Asigna el nombre del producto a la consulta
            ResultSet rsProd = psProd.executeQuery(); // Ejecuta la consulta

            // Si el producto no existe, se muestra un mensaje de error
            if (!rsProd.next()) {
                out.println("<div style='text-align:center; background-color:#ffebee; padding:20px; border-radius:10px;'>"
                        + "<h2 style='color:#d32f2f;'>Producto no encontrado</h2></div>");
                return; // Sale del método sin continuar
            }

            // Se obtiene el ID del producto
            int productoId = rsProd.getInt("id");

            // Insertar la compra en la base de datos
            PreparedStatement psCompra = conn.prepareStatement(
                "INSERT INTO compras (usuario_id, producto_id) VALUES (?, ?)");
            psCompra.setInt(1, usuarioId); // Asigna el ID del usuario
            psCompra.setInt(2, productoId); // Asigna el ID del producto
            psCompra.executeUpdate(); // Ejecuta la inserción

            // Mostrar página de confirmación estilizada
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Compra Exitosa</title>");
            out.println("<style>"); // Inicio de estilos CSS
            out.println("body { font-family: Arial, sans-serif; background-color: #f5f5f5; margin: 0; padding: 20px; }");
            out.println(".container { max-width: 600px; margin: 50px auto; background: white; padding: 30px; border-radius: 15px; box-shadow: 0 5px 15px rgba(0,0,0,0.1); text-align: center; }");
            out.println(".success-icon { color: #4CAF50; font-size: 48px; margin-bottom: 20px; }");
            out.println("h2 { color: #4CAF50; font-size: 28px; margin-bottom: 20px; }");
            out.println(".product-info { background-color: #f9f9f9; padding: 15px; border-radius: 8px; margin: 20px 0; }");
            out.println(".address { font-weight: bold; color: #333; }");
            out.println(".reminder { margin-top: 25px; padding-top: 15px; border-top: 1px solid #eee; color: #666; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<div class='success-icon'>✓</div>"); // Ícono de éxito
            out.println("<h2>¡Compra exitosa!</h2>"); // Título
            out.println("<div class='product-info'>");
            out.println("<p>Has comprado una <strong>" + producto + "</strong>.</p>"); // Producto comprado
            out.println("<p>Pronto llegará a tu casa: <span class='address'>" + direccion + "</span></p>"); // Dirección
            out.println("</div>");
            out.println("<div class='reminder'>");
            out.println("<p>Recuerda pagar al domiciliario.</p>"); // Nota al usuario
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

        } catch (SQLException e) {
            // En caso de error en la base de datos, se muestra un mensaje de error
            out.println("<div style='text-align:center; background-color:#ffebee; padding:20px; border-radius:10px;'>"
                    + "<h2 style='color:#d32f2f;'>Error en la compra: " + e.getMessage() + "</h2></div>");
        }
    }
}
