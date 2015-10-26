package mx.com.meda;

import org.apache.log4j.Logger;

import javax.annotation.Resource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DataWrapper {

	Logger log = Logger.getLogger(this.getClass());

	public void seleccionarPrueba() throws SQLException {

		//Properties props = new Properties();

		//props.put(Context.INITIAL_CONTEXT_FACTORY, org.jboss.naming.remote.client.InitialContextFactory.class.getName());
		//props.put("jboss.naming.client.ejb.context", true);
		//props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		//props.put(Context.PROVIDER_URL,"remote://localhost:4447");
		//props.put("jboss.naming.client.ejb.context", true);

		try {
			//InitialContext ctx = new InitialContext(props);
			InitialContext ctx = new InitialContext();
			log.debug("Se solicitará el datasource.");
			DataSource ds = (DataSource) ctx.lookup("java:/medaDS");
			log.debug("Se obtuvo el datasource.");
			Connection con = ds.getConnection();
			Statement stm = con.createStatement();
			String sql = "select * from GenArchivos;";
			log.debug("Se ejecutará la siguiente consulta: "+sql);
			ResultSet rs = stm.executeQuery(sql);
			log.debug("Se ejecutó la onsulta: "+sql);
			if(!rs.isBeforeFirst()) {
				log.error("No hay registros en la consulta: "+sql);
			} else {
				log.info(rs.getString(1));
			}
		} catch (NamingException ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
	}
}