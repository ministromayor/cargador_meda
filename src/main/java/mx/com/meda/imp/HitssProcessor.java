package mx.com.meda.imp;

import org.apache.log4j.Logger;
import mx.com.meda.Processor;
import mx.com.meda.DataWrapper;
import mx.com.meda.SFTPClient;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Properties;
import java.util.regex.Pattern;

import java.util.TimeZone;
import java.util.Date;
import java.util.List;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.JSchException;

import mx.com.meda.Socio;

public class HitssProcessor implements Processor {

	Logger log = Logger.getLogger(this.getClass());
	private SFTPClient cliente;
	private DataWrapper dw;
	Socio socio = Socio.HITSS;


	private String altas_in_separador = "|";
	private boolean altas_in_header = false;
	private boolean altas_in_trailer = false;
	private int altas_in_campos = 7;
	private String altas_in_nombre = null;

	//Variables de configuración para los archivos de salida.
	private String altas_out_separador = "|";
	private boolean altas_out_header = false;
	private boolean altas_out_trailer = false;
	private String altas_out_nombre = "PSF_RespuestaRHYYYYMMDDHHmm.acc";
	


	private final String MEDA_HITSS_PROPERTIES_FILENAME = this.socio.getNombre().toLowerCase()+".properties";

	private void loadConfiguration() throws IOException {
		Properties settings = new Properties();
		settings.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(MEDA_HITSS_PROPERTIES_FILENAME));

		altas_in_separador = settings.getProperty("altas.in.separador");
		altas_in_header = new Boolean(settings.getProperty("altas.in.header")).booleanValue();
		altas_in_trailer = new Boolean(settings.getProperty("altas.in.trailer")).booleanValue();
		altas_in_campos = new Integer(settings.getProperty("altas.in.campos")).intValue();
		altas_in_nombre = settings.getProperty("altas_in_nombre");

		altas_out_separador = settings.getProperty("altas.out.separador");
		altas_out_header = new Boolean(settings.getProperty("altas.out.header"));
		altas_out_trailer = new Boolean(settings.getProperty("altas.out.trailer"));
		altas_out_nombre = settings.getProperty("altas.out.nombre");

	}
	
	public HitssProcessor(SFTPClient sftp, DataWrapper dw) throws IOException {
		this.cliente = sftp;
		this.dw = dw;
	}

	public HitssProcessor() {
		try {
			loadConfiguration();
			this.cliente = new SFTPClient(socio);
			this.dw = new DataWrapper(socio);
		} catch (JSchException ex ) {
			log.error("Ocurrio un error al generar el cliente SFTP.");
			log.debug(ex.getMessage());
			//Manejar las excepciones futuras en el manejo de una clase instanciada con errores.
		} catch (IOException ex) {
			log.error("No se pudo cargar la configuración del módulo desde el archivo: "+MEDA_HITSS_PROPERTIES_FILENAME);
			log.error(ex.getMessage());
		}

	}

	public boolean procesarEntrada() {
		log.debug("Se comenzará la lectura del archivo de entrada.");
		try {
			if( cliente.conectar() )  {
				String file_name = cliente.lastAddedInFileName();
				log.info("Se cargará el achivo: "+file_name);
				BufferedReader br = new BufferedReader(new InputStreamReader(cliente.readLastInFile()));
				String linea = null;	
				while( (linea = br.readLine()) != null ) {
					log.debug(linea);
					String[] values = new String[altas_in_campos+1];
					values[0] = file_name;
					log.debug("Se separará la cadena con \""+altas_in_separador+"\"");
					String[] tokens = linea.split(Pattern.quote(altas_in_separador));
					if(tokens.length != altas_in_campos) {
						log.error("La linea contiene "+tokens.length+" elementos pero se esperaba que tuviera "+altas_in_campos);
					} else {
						System.arraycopy(tokens, 0, values, 1, altas_in_campos);
						dw.cargarLinea(3, values);
					}
				}
				if( dw.procArchivoCarga(3, file_name) ) {
					this.procesarSalida();
				} else {
					log.error("No se procesará salida debido a que ocurrió un error durante el proceso de entrada.");
				}
				br.close();
				cliente.desconectar();
			}
		} catch( SftpException ex ) {
			ex.printStackTrace();
		} finally {
			return true;
		}
	}

	public boolean procesarSalida() { 
		log.debug("Se comenzará la generación del archivo de salida.");
		try {
			String date_format = "yyyyMMddHHmm";
			DateFormat df = new SimpleDateFormat(date_format);
			df.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
			String date = df.format(new Date());
			
			String out_filename = "PSF_RespuestaRH"+date+".acc";
			log.info("Se reportará el siguiente archivo: "+out_filename);

			List<Object[]> filas = dw.selArchivoSalida(4);
			if(!filas.isEmpty()) {
				log.info("Se deben procesar los registros para generar el archivo.");
			} else {
				log.warn("No se obtuvieron registros para generar un archivo de respuesta.");
			}
		} catch( Exception ex ) {
			ex.printStackTrace();
		} finally {
			return true;
		}
	}

	public void release() {
		dw.release();
	}

}