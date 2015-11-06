package mx.com.meda.imp;

import org.apache.log4j.Logger;
import mx.com.meda.Processor;
import mx.com.meda.DataWrapper;
import mx.com.meda.SFTPClient;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.Properties;
import java.util.regex.Pattern;

import java.util.TimeZone;
import java.util.Date;
import java.util.List;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import mx.com.meda.Socio;
import mx.com.meda.TipoDeArchivo;
import mx.com.meda.MFTPClient;

public class SanbornsProcessor extends AliadoProcessor implements Processor {

	MFTPClient ftp_client = null;

	public SanbornsProcessor() {
		super(Socio.SANBORNS);
		this.cliente = null;
		ftp_client = new MFTPClient(Socio.SANBORNS);
		log = Logger.getLogger(this.getClass());
	}

	public boolean procesarEntrada() {
		log.debug("Se comenzará la lectura del archivo de entrada.");
		int lines = 0;
		String[] trailer = null;
		try {
			if( ftp_client.conectar() )  {
				String file_name = ftp_client.lastAddedInFileName(buildInputFilename());
				log.info("Se cargará el achivo: "+file_name);
				BufferedReader br = new BufferedReader(new InputStreamReader(ftp_client.readLastInFile(file_name)));
				String linea = null;	
				while( (linea = br.readLine()) != null ) {
					log.debug(">>"+linea);
					String[] values = new String[in_campos+1];
					values[0] = file_name;
					log.debug("Se separará la cadena con \""+in_separador+"\"");
					String[] tokens = linea.split(Pattern.quote(in_separador));

					if(((tokens.length != in_campos) && br.ready()) ||
						((!br.ready() && !in_trailer) && (tokens.length != in_campos))) {
						log.error("La linea ["+linea+"] contiene "+tokens.length+" elementos pero se esperaba que tuviera "+in_campos);
						tokens = null;
					} else if(!br.ready() && in_trailer) {
						log.debug("Se considerará la cadena "+linea+" como trailer.");
						trailer = tokens;
						tokens = null;
					} 
					if(tokens != null ) {
						System.arraycopy(tokens, 0, values, 1, in_campos);
						dw.cargarLinea(TipoDeArchivo.RECIBE_TICKETS.getId(), values);
						lines++;
					}
				}
				if( validarTrailer(trailer, lines) && dw.procArchivoCarga(TipoDeArchivo.RECIBE_TICKETS.getId(), file_name) ) {
					log.info("Se completó la recepción y procesamiento de archivos de datos.");
				} else {
					log.error("No se procesará salida debido a que ocurrió un error durante el proceso de entrada.");
				}
				br.close();
				ftp_client.desconectar();
			}
		} catch( Exception ex ) {
			log.error("No se puedo procesar la entrada.");
			log.warn(ex.getMessage());
			ex.printStackTrace();
		} finally {
			return true;
		}
	}

	public boolean procesarSalida() {
		log.debug("Se comenzará la generación del archivo de salida.");
		try {
			if(ftp_client.conectar()) {
				String out_filename = buildOutputFilename();
				List<Object[]> filas = dw.selArchivoSalida(TipoDeArchivo.RESPUESTA_TICKETS.getId());
				if(!filas.isEmpty()) {
					for(Object[] arreglo : filas) {
						StringBuilder sb = new StringBuilder();
						for(int i = 0; i < (out_campos-1); i++) {
							sb.append(arreglo[i]);
							sb.append("|");
						}
						sb.append(arreglo[out_campos-1]);
						String linea = sb.toString();
						log.debug("<<"+linea);
						escribirRespuesta(linea);
					}
					InputStream salida = recuperarRespuesta();
					ftp_client.uploadOutFile(salida, out_filename);
					log.debug("Se almacenó el archivo en el servidor FTP.");
				} else {
					log.warn("No se obtuvieron registros para generar un archivo de respuesta.");
				}
				ftp_client.desconectar();
			}
		} catch( Exception ex ) {
			log.error("Error al abrir o cerrar la conexión con el sftp.");
			log.error(ex.getMessage());
		} finally {
			return true;
		}
	}

	private String buildInputFilename() {
		String date_format = "ddMMyyyy";
		DateFormat df = new SimpleDateFormat(date_format);
		df.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
		String date = df.format(new Date());
		in_nombre = "SNB"+date+".acc";
		return in_nombre;
	}

	private String buildOutputFilename() {
		String date_format = "ddMMyyyy";
		DateFormat df = new SimpleDateFormat(date_format);
		df.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
		String date = df.format(new Date());
		out_nombre = "SNB"+date+"RES.acc";
		log.info("Se reportará el siguiente archivo: "+out_nombre);
		return out_nombre;
	}

	private boolean validarTrailer(String[] tokens, int registros) {
		boolean flag = false;
		if(in_trailer) {
			int lineas_recibidas = Integer.valueOf(tokens[0]).intValue();
			if((tokens.length == in_t_campos) && (lineas_recibidas == registros)) {
				flag = true;
			} else {
				flag = false;
			}
			log.info("El trailer es: "+ (flag ? "Valido" : "Erroneo") +" para "+registros+" registros.");
		} else {
			flag = true;
		}
		return flag;
	}

}