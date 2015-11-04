package mx.com.meda;

import org.apache.log4j.Logger;

import java.util.Properties;
import java.util.TimeZone;
import java.util.Vector;
import java.util.Date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPConnectionClosedException;

import java.io.InputStream;
import java.io.IOException;

public class MFTPClient {

	private String 	cfg_host 	= "127.0.0.1";
	private String 	cfg_path 	= "./Entrada";
	private int 		cfg_port 	= 21;
	private String 	cfg_usuario = "sftpptos";
	private String 	cfg_password = "stppt0s";
	private int 		cfg_timeout = 15000;
	private String 	cfg_in_dir	= "/Entrada";
	private String 	cfg_out_dir	= "/Salida";

	private FTPClient ftp = null;

	private static final String MEDA_FTP_PROPERTIES_FILENAME = "ftp-client.properties";
	private Logger log = Logger.getLogger(this.getClass());

	public MFTPClient(Socio peer) {
		try {
			loadConfiguration(peer.getNombre());
		}catch(IOException ex) {
			log.error("No se pudo cargar el archivo de configuración, se utilizarán los ajustes por defecto.");
		}
	}

	private void loadConfiguration(String peerName) throws IOException {
		Properties settings = new Properties();
		settings.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(MEDA_FTP_PROPERTIES_FILENAME));

		String lc_peerName = peerName.toLowerCase();
		String[] peer_properties_keys = new String[]{
			lc_peerName + ".host",
			lc_peerName + ".port",
			lc_peerName + ".user",
			lc_peerName + ".pasw",
			lc_peerName + ".timeout",
			lc_peerName + ".in",
			lc_peerName + ".out"
		};

		String val = null;

		val = settings.getProperty(lc_peerName + ".host");
		cfg_host = val != null ? val : cfg_host;

		val = settings.getProperty(lc_peerName + ".port");
		cfg_port = val != null ? Integer.valueOf(val).intValue() : cfg_port;

		val = settings.getProperty(lc_peerName + ".user");
		cfg_usuario = val != null ? val : cfg_usuario;

		val = settings.getProperty(lc_peerName + ".pasw");
		cfg_password = val != null ? val : cfg_password;

		val = settings.getProperty(lc_peerName + ".timeout");
		cfg_timeout = val != null ? Integer.valueOf(val).intValue() : cfg_timeout;

		val = settings.getProperty(lc_peerName + ".in");
		cfg_in_dir = val != null ? val : cfg_in_dir;

		val = settings.getProperty(lc_peerName + ".out");
		cfg_out_dir = val != null ? val : cfg_out_dir;
	}
	
	public boolean conectar() {
		boolean flag = false;

		ftp = new FTPClient();
		FTPClientConfig config = new FTPClientConfig();
		ftp.configure(config);

		boolean error = false;
		try {
			int reply;
			ftp.connect(cfg_host, cfg_port);
			reply = ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				log.error("El servidor "+cfg_host+" rechazó la conexión. ("+reply+").");
			}
      } catch(IOException ex) {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException f) { }
			}
			log.error("No se pudo realizar la conexión al FTP.");
			log.error(ex.getMessage());
		}

		//evitar el espaguetti.
		try {
			if (!ftp.login(cfg_usuario, cfg_password)) {
				ftp.logout();
				log.error("Fallo la autenticación con el FTP.");
			} else {
				log.debug("Ahora la sesión se encuentra conectada.");
				flag = true;
			}
		} catch(IOException ex) {
			try{ 
				ftp.logout();
				ftp.disconnect();
			} catch(IOException f) { }
			log.error("No se pudo completar la conexión con el ftp.");
		}
		//hacerlo mas efectivo.
		return flag;
	}

	// de momento no tiene sentido este metodo, lo dejo para mantener la consistencia y quizá definir una interfas.
	public String lastAddedInFileName() {
		return lastAddedFileName(cfg_in_dir, "*");
	}

	public String lastAddedInFileName(String name) {
		// A diferencia de la versión mas sofisticada del cliente SFTP 
		// esta solo construye (de momento) la ruta asumiento que el archivo existe. 
		// se mantiene para la consistencia de la interfaz y no impactar al procesador de aliados.
		// una excepción no est crítica en este punto.
		return name;
	}

	// Este metodo tambien se mantiene por consistencia.
	private String lastAddedFileName(String rootRelativeDir, String file_name) {
		return rootRelativeDir+"/"+file_name;
	}

	// de momento no tiene sentido este metodo, lo dejo para mantener la consistencia y quizá definir una interfas.
	public InputStream readLastInFile() throws IOException {
		return readLastInFile("*");
	}

	// este metodo si se usa y es el importante para la lectura del archivo.
	//////////////////////////////////////////////////////////////////////
	public InputStream readLastInFile(String name) throws IOException {
		InputStream is = null;
		String lf = this.lastAddedFileName(cfg_in_dir, name);
		try {
			is = ftp.retrieveFileStream(name);
		} catch ( FTPConnectionClosedException ex ) {
			log.error("No se pudo abrir un flujo de entrada desde el FTP.");
		} finally {
			return is;
		}
	}

	// este metodo también es importante para subir los archivos al ftp.
	public void uploadOutFile(InputStream data, String name) {
		String path = this.cfg_out_dir+"/"+name;
		log.info("Se subirá el archivo: "+path);
		try {
			ftp.storeFile(path, data);
			data.close();
		} catch( IOException ex ) {
			log.error("No se pudo almacenar el archivo de salida del proceso.");
			log.error(ex.getMessage());
		}
	}

	public boolean desconectar() throws IOException {
		if(!ftp.completePendingCommand()) {
			log.error("La carga o descarga  del FTP fallo al cierre.");
		}
		ftp.logout();
		ftp.disconnect();
		return true;
	}

}