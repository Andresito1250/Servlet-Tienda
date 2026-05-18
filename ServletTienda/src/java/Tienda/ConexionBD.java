// Define que esta clase pertenece al paquete llamado 'Tienda'
package Tienda;
// Importa la clase Connection para manejar conexiones a bases de datos
import java.sql.Connection;
// Importa la clase DriverManager para gestionar las conexiones JDBC
import java.sql.DriverManager;
// Importa la clase SQLException para manejar errores de base de datos
import java.sql.SQLException;
// Define la clase ConexionBD
public class ConexionBD {
    // Constante que almacena la URL de conexión a la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/tienda";
     // Constante que almacena el nombre de usuario de la base de datos
    private static final String USUARIO = "root";
       // Constante que almacena la contraseña de la base de datos (vacía en este caso)
    private static final String CONTRASENA = ""; 
// Método público y estático que devuelve una conexión a la base de datos
    public static Connection obtenerConexion() throws SQLException {
        try {
             // Carga el driver de MySQL de forma explícita
            Class.forName("com.mysql.cj.jdbc.Driver"); // Carga el driver
             // Retorna una conexión utilizando la URL, usuario y contraseña definidos
            return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            // Si no se encuentra el driver, lanza una excepción SQLException personalizada
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver de MySQL", e);
        }
    }
}
