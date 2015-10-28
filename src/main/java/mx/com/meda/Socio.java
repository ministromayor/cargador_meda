package mx.com.meda;

public enum Socio{
	BABY_TOWN("BABY TOWN", 1),
	BASIC_PUFF("BASIC PUFF", 2),
	BIOMEDICA_DE_REFERENCIA("BIOMEDICA DE REFERENCIA", 3),
	CHEDRAUI("CHEDRAUI", 4),
	CIRCULO_K("CIRCULO K", 5),
	CROSSFIT_RAM("CROSSFIT RAM", 5),
	DENTALIA("DENTALIA", 6),
	ECO_SALADS("ECO SALADS", 6),
	EDUCANET_("EDUCANET_", 7),
	EL_CUEVON("EL CUEVON", 8),
	EL_SURTIDOR("EL SURTIDOR", 9),
	EPS_SERVICIO_AUTOMOTRIZ("EPS SERVICIO AUTOMOTRIZ", 10),
	ESTETICA_SHARM("ESTETICA SHARM", 11),
	FIESTA_AMERICANA("FIESTA AMERICANA", 12),
	FIESTA_AMERICANA_GRAND("FIESTA AMERICANA GRAND", 13),
	FIESTA_INN("FIESTA INN", 14),
	GREEN_LIGHT_GOURMET_PIZZA("GREEN LIGHT GOURMET PIZZA", 15),
	GYMBOREE("GYMBOREE", 16),
	HIDROSINA("HIDROSINA", 17),
	IAVE("IAVE", 18),
	IHOP("IHOP", 19),
	INMODA("INMODA", 20),
	JUGUETERIAS_JULIO_CEPEDA("JUGUETERIAS JULIO CEPEDA", 21),
	KINDERGYM("KINDERGYM", 22),
	LE_PARISIEN("LE PARISIEN", 23),
	LIBRERIAS_PORRUA("LIBRERIAS PORRUA", 24),
	LIVE_AQUA("LIVE AQUA", 25),
	MEXICAN_FOOD_TOURS("MEXICAN FOOD TOURS", 26),
	MULTIFARMACIAS("MULTIFARMACIAS", 27),
	MULTIFARMACIAS_GENERICOS("MULTIFARMACIAS GENERICOS", 28),
	ONE("ONE", 29),
	OSTAR("OSTAR", 30),
	PAKMAIL("PAKMAIL", 31),
	PUNTO_VERDE("PUNTO VERDE", 32),
	SUPERCOLCHONES("SUPERCOLCHONES", 33),
	THE_HAMBURGUER_CLUB("THE HAMBURGUER CLUB", 34),
	TIENDAS_ATLAS("TIENDAS ATLAS", 35),
	TINTORERIA_REAL("TINTORERIA REAL", 36),
	TUR_TUX("TUR TUX", 37),
	VIVE_GADGET("VIVE GADGET", 38),
	WA_TEPPAN("WA TEPPAN", 39),
	WOK_INN("WOK INN", 40),
	TELCEL("TELCEL", 41),
	DECOMPRAS("DECOMPRAS", 42),
	SANBORNS("SANBORNS", 43),
	HITSS("HITSS", 44);

	private int id= 1;
	private String nombre;

	Socio(String nombre, int id) {
		this.id = id;
		this.nombre = nombre;
	}

	public int getId() {
		return this.id;
	}
	public String getNombre() {
		return this.nombre;
	}


}