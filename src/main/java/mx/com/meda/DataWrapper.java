package mx.com.meda;

import org.apache.log4j.Logger;

import javax.annotation.Resource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Types;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DataWrapper {

	Logger log = Logger.getLogger(this.getClass());

	private DataSource ds = null;
	private Connection con = null;
	private String peerId = null;
	private Socio peer = null;


	public DataWrapper(Socio peer) {
		this(peer.getNombre());
		this.peer = peer;
	}


	public DataWrapper(String peerId) {
		try {
			InitialContext ctx = new InitialContext();
			log.debug("Se solicitará el datasource.");
			ds = (DataSource) ctx.lookup("java:/medaDS");
			log.info("Se obtuvo el datasource.");
			con = ds.getConnection();
		} catch (NamingException ex) {
			log.error("No se pudo obtener una instancia del DataSource configurado.");
			log.debug(ex.getMessage());
		} catch (SQLException ex ) {
			log.error("No de pudo establecer la conexión con la instancia del datasource.");
			log.debug(ex.getMessage());
		}
	}

	public void release() {
		if( con != null ) {
			try {
				con.close();
				con = null;
			} catch(SQLException ex) { 
				log.error("No se pudo cerrar la conexión con la base de datos");
				log.debug(ex.getMessage());
			}
		}
	}

	public void seleccionarPrueba() throws SQLException {
		Statement stm = null;
		try {
			stm = con.createStatement();
			String sql = "select * from CatAliado;";
			log.debug("Se ejecutará la siguiente consulta: "+sql);
			ResultSet rs = stm.executeQuery(sql);
			log.info("Se ejecutó la consulta: "+sql);
			if(!rs.isBeforeFirst()) {
				log.error("No hay registros en la consulta: "+sql);
			} else {
				while(rs.next()) {
					StringBuffer sb = new StringBuffer();
					sb.append(rs.getString(1));
					sb.append(" | ");
					sb.append(rs.getString(2));
					sb.append(" | ");
					sb.append(rs.getString(3));
					sb.append(" | ");
					sb.append(rs.getString(4));
					sb.append(" | ");
					sb.append(rs.getString(5));
					sb.append(" | ");
					sb.append(rs.getString(6));
					sb.append(" | ");
					sb.append(rs.getString(7));
					sb.append(" | ");
					sb.append(rs.getString(8));
					sb.append(" | ");
					sb.append(rs.getString(9));
					sb.append(" | ");
					sb.append(rs.getString(10));
					sb.append(" | ");
					sb.append(rs.getString(11));
					sb.append(" | ");
					sb.append(rs.getString(12));
					sb.append(" | ");
					log.debug(sb.toString());
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		} finally {
			stm.close();
			stm = null;
		}
	}

	public void cargarLinea(int tipo_de_archivo, String[] values) {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("insert into CrgArchivos values(?,?,");
			for(int i = 0; i < (values.length)-1; i++) {
				sb.append("?,");
			}
			sb.append("?);");

			String ps_material = "insert into CrgArchivos values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			log.info("El material para la sentencia peparada es: "+ps_material);

			PreparedStatement pstm = con.prepareStatement(ps_material);
			//optimizar este segundo ciclo.
			pstm.setInt(1, peer.getId());
			pstm.setInt(2, tipo_de_archivo);
			for(int i = 0; i < values.length; i++) {
				pstm.setString(i+3, values[i]);
				log.debug("Estableciendo el valor del campo: "+(i+3));
			}
			int nulls_offset = (values.length + 3);
			for(int i = nulls_offset; i <= 14; i++) {
				pstm.setNull(i, Types.VARCHAR);
				log.debug("Estableciendo nulo el valor del campo: "+(i));
			}
			//optimizar utilizando addBatch()
			pstm.executeUpdate();
		} catch (SQLException ex) {
			log.error("No se pudo insertar un registro.");
			log.error(ex.getMessage());
		}
	}

	public void performCommit() {
		try {
			con.commit();
		} catch (SQLException ex) {
			log.error("No se pudo hacer commit a la transacción.");
			log.error(ex.getMessage());
		}
	}

}