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

public class AliadoProcessor {

	protected Logger log = null;
	Socio socio = null;

	protected SFTPClient cliente;
	protected DataWrapper dw;

	protected String altas_in_separador = "|";
	protected boolean altas_in_header = false;
	protected boolean altas_in_trailer = false;
	protected int altas_in_campos = 7;
	protected String altas_in_nombre = "";

	//Variables de configuración para los archivos de salida.
	protected String altas_out_separador = "|";
	protected boolean altas_out_header = false;
	protected boolean altas_out_trailer = false;
	protected String altas_out_nombre = "PSF_RespuestaRHyyyyMMddHHmm.acc";

	protected String MEDA_PROPERTIES_FILENAME = "default.properties";

	private void loadConfiguration() throws IOException {
		Properties settings = new Properties();
		settings.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(MEDA_PROPERTIES_FILENAME));

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
	
	public AliadoProcessor(SFTPClient sftp, DataWrapper dw) throws IOException {
		this.cliente = sftp;
		this.dw = dw;
	}

	public AliadoProcessor(Socio socio) {
		this.socio = socio;
		MEDA_PROPERTIES_FILENAME = socio.getNombre().toLowerCase()+".properties";
		try {
			loadConfiguration();
			this.cliente = new SFTPClient(socio);
			this.dw = new DataWrapper(socio);
		} catch (JSchException ex ) {
			log.error("Ocurrio un error al generar el cliente SFTP.");
			log.debug(ex.getMessage());
			//Manejar las excepciones futuras en el manejo de una clase instanciada con errores.
		} catch (IOException ex) {
			log.error("No se pudo cargar la configuración del módulo desde el archivo: "+MEDA_PROPERTIES_FILENAME);
			log.error(ex.getMessage());
		}

	}

	public void release() {
		dw.release();
	}

}