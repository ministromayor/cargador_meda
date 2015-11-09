package mx.com.meda;

import org.apache.log4j.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

import java.sql.SQLException;

import java.io.IOException;
import java.io.InputStream;
import com.jcraft.jsch.JSchException;

import mx.com.meda.imp.SanbornsProcessor;


@Singleton(name = "JobCargador")
public class JobCargador {

	Logger log = Logger.getLogger(this.getClass());

	/*@Schedule(second="15", minute="*", hour="*", persistent=false)
	public void hitssAltas() {
		Processor proc = ProcessorFactory.getProcessorInstance(Socio.HITSS);
		proc.procesarEntrada();
		proc.release();
	}*/

	/*@Schedule(second="60", minute="*", hour="*", persistent=false)
	public void hitssAcreditaciones() {
		Processor proc = ProcessorFactory.getProcessorInstance(Socio.HITSS_ACREDITACIONES);
		proc.procesarEntrada();
		proc.release();
	}*/

	/*@Schedule(second="10", minute="*", hour="*", persistent=false)
	public void ostar() {
		Processor proc = ProcessorFactory.getProcessorInstance(Socio.OSTAR);
		proc.procesarEntrada();
		proc.release();
	}*/

	/*@Schedule(second="15", minute="*", hour="*", persistent=false)
	public void iave() {
		Processor proc = ProcessorFactory.getProcessorInstance(Socio.IAVE);
		proc.procesarEntrada();
		proc.release();
	}*/

	/*@Schedule(second="00", minute="*", hour="*", persistent=false)
	public void chedrauiIn() {
		Processor proc = ProcessorFactory.getProcessorInstance(Socio.CHEDRAUI);
		proc.procesarEntrada();
		proc.release();
	}

	@Schedule(second="30", minute="*", hour="*", persistent=false)
	public void chedrauiOut() {
		Processor proc = ProcessorFactory.getProcessorInstance(Socio.CHEDRAUI);
		proc.procesarSalida();
		proc.release();
	}*/

	@Schedule(second="15", minute="*", hour="*", persistent=false)
	public void sanbornsIn() {
		Processor proc = ProcessorFactory.getProcessorInstance(Socio.SANBORNS);
		proc.procesarEntrada();
		proc.release();
	}

	@Schedule(second="45", minute="*", hour="*", persistent=false)
	public void sanbornsOut() {
		Processor proc = ProcessorFactory.getProcessorInstance(Socio.SANBORNS);
		proc.procesarSalida();
		proc.release();
	}

	/*@Schedule(second="40", minute="*", hour="*", persistent=false)
	public void inbursa() {
		Processor proc = ProcessorFactory.getProcessorInstance(Socio.INBURSA);
		proc.procesarEntrada();
		proc.release();
	}

	@Schedule(second="40", minute="*", hour="*", persistent=false)
	public void hidrosina() {
		Processor proc = ProcessorFactory.getProcessorInstance(Socio.HIDROSINA);
		proc.procesarEntrada();
		proc.release();
	}*/


}
