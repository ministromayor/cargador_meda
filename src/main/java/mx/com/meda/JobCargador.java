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

	@Schedule(second="*/30", minute="*", hour="*", persistent=false)
	public void trabajo() {
		Processor proc = ProcessorFactory.getProcessorInstance("HITSS");
		proc.procesarEntrada();
	}

	@Schedule(second="0", minute="*/30", hour="*/4", persistent=false)
	public void tarea2() {
		Processor proc = ProcessorFactory.getProcessorInstance("HITSS");
		proc.procesarEntrada();
	}


}
