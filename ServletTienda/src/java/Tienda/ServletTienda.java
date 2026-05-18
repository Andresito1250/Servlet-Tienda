package Tienda; // Define el paquete al que pertenece esta clase (Tienda)

// Importaciones necesarias para trabajar con archivos, base de datos, servlets y HTTP
import java.io.*; // Para la entrada/salida (PrintWriter, IOException, etc.)
import java.sql.*; // Para usar JDBC (Connection, PreparedStatement, ResultSet, etc.)
import jakarta.servlet.*; // Para trabajar con servlets (ServletException, etc.)
import jakarta.servlet.http.*; // Para trabajar con objetos HTTP específicos (HttpServlet, HttpServletRequest, etc.)

// Definición de la clase ServletTienda que hereda de HttpServlet
public class ServletTienda extends HttpServlet {

    // Método principal que atiende tanto peticiones GET como POST
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Se obtienen los parámetros enviados desde el formulario HTML
        String nombre = request.getParameter("nombre");      // Campo nombre del usuario
        String email = request.getParameter("email");        // Campo email del usuario
        String password = request.getParameter("passwd");    // Campo contraseña
        String direccion = request.getParameter("direccion");// Campo dirección

        // Se establece el tipo de contenido de la respuesta como HTML con codificación UTF-8
        response.setContentType("text/html;charset=UTF-8");

        // Se crea un PrintWriter para enviar contenido de respuesta al navegador
        try (PrintWriter out = response.getWriter()) {

            // Se intenta establecer una conexión con la base de datos
            try (Connection conn = ConexionBD.obtenerConexion()) {

                // Consulta SQL para insertar los datos del usuario en la tabla "usuarios1"
                String sql = "INSERT INTO usuarios1 (nombre, email, contrasena, direccion) VALUES (?, ?, ?, ?)";

                // Se prepara la sentencia SQL y se solicita que devuelva las claves generadas (ID autoincremental)
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                // Se asignan los valores del formulario a la sentencia SQL
                stmt.setString(1, nombre);     // Primer parámetro: nombre
                stmt.setString(2, email);      // Segundo parámetro: email
                stmt.setString(3, password);   // Tercer parámetro: contraseña
                stmt.setString(4, direccion);  // Cuarto parámetro: dirección

                // Se ejecuta la inserción y se obtiene el número de filas afectadas
                int filas = stmt.executeUpdate();

                // Si al menos una fila fue insertada correctamente
                if (filas > 0) {

                    // Se obtienen las claves generadas (en este caso, el ID del nuevo usuario)
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        int usuarioId = rs.getInt(1); // Se extrae el ID generado

                        // Se guarda el ID del usuario en la sesión del navegador
                        HttpSession session = request.getSession();
                        session.setAttribute("usuario_id", usuarioId);
                    }

                    // Redirige al usuario al catálogo de productos tras registrarse
                    response.sendRedirect("catalogo.html");
                } else {
                    // Si no se insertó ninguna fila, muestra un mensaje de error
                    out.println("<h2>Error al registrar usuario</h2>");
                }

            } catch (SQLException e) {
                // Si ocurre un error de base de datos, se muestra un mensaje con la excepción
                out.println("<h2>Error de conexión: " + e.getMessage() + "</h2>");
            }
        }
    }

    // Método que maneja peticiones POST del formulario
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response); // Llama al método común para procesar la solicitud
    }

    // Método que maneja peticiones GET (en caso de que se acceda por URL directa)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response); // También usa el método común
    }
}
