package app.sembrando.vidas.java_class;

public class Variables {


    // Production URL - Railway deployment
    private final static String url = "https://yoplantounarbolito-production.up.railway.app/api";

    // Local development URL (for testing with emulator)
    //private final static String url = "http://10.0.2.2:8000/api";
    private final static String PLANTAR = "Plantar", REGAR = "Regar", LIMPIEZA = "Limpieza", ABONO = "Abono",
            AGARRE = "Agarre", JUEGOS = "Juegos";

    public String getPLANTAR() {
        return PLANTAR;
    }

    public String getREGAR() {
        return REGAR;
    }

    public String getLIMPIEZA() {
        return LIMPIEZA;
    }

    public String getABONO() {
        return ABONO;
    }

    public String getAGARRE() {
        return AGARRE;
    }

    public String getJUEGOS() {
        return JUEGOS;
    }

    public String getUrl() {
        return url;
    }
}
