package Tienda; // Define el paquete donde se encuentra esta clase

// Importación de clases necesarias
import java.io.*; // Para trabajar con entrada/salida, como PrintWriter
import java.sql.*; // Para manejar bases de datos (JDBC)
import jakarta.servlet.*; // Para funcionalidades de Servlets
import jakarta.servlet.http.*; // Para manejar peticiones/respuestas HTTP

// Clase AgregarProducto que extiende HttpServlet
public class AgregarProducto extends HttpServlet {

    // Método que maneja solicitudes POST (por ejemplo, desde un formulario)
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Obtiene el nombre del producto enviado desde el formulario
        String producto = request.getParameter("producto");

        // Define el tipo de contenido de la respuesta como HTML con codificación UTF-8
        response.setContentType("text/html;charset=UTF-8");

        // Se usa try-with-resources para manejar automáticamente el cierre de PrintWriter
        try (PrintWriter out = response.getWriter()) {

            // Segundo try para manejar la conexión a la base de datos
            try (Connection conn = ConexionBD.obtenerConexion()) {
                // Sentencia SQL para insertar un nuevo producto en la tabla 'productos'
                String sql = "INSERT INTO productos (nombre) VALUES (?)";
                PreparedStatement stmt = conn.prepareStatement(sql); // Prepara la consulta
                stmt.setString(1, producto); // Asigna el valor del producto al parámetro de la consulta

                int filas = stmt.executeUpdate(); // Ejecuta la consulta (INSERT) y devuelve cuántas filas se modificaron

                if (filas > 0) {
                    // Si se insertó al menos una fila, se muestra un mensaje de éxito
                    out.println("<h2>Producto agregado: " + producto + "</h2>");
                    out.println("<a href='catalogo.html'>Volver al catálogo</a>");
                } else {
                    // Si no se insertó ninguna fila, se muestra un mensaje de error
                    out.println("<h2>Error al agregar producto</h2>");
                }
            } catch (SQLException e) {
                // En caso de error en la conexión o en la consulta, se muestra el mensaje
                out.println("<h2>Error de conexión: " + e.getMessage() + "</h2>");
            }
        }
    }
}
