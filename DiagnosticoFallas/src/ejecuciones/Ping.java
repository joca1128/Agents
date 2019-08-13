package ejecuciones;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Ping {

  public static String runSystemCommand(String command) {
	  	int cont=0;
		try {
			
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader inputStream = new BufferedReader(
					new InputStreamReader(p.getInputStream()));

			String s = "";
			// reading output stream of the command
			while ((s = inputStream.readLine()) != null) {
				System.out.println(s);;
				if(s.matches("    Paquetes: enviados = 4, recibidos = 4, perdidos = 0")) {
					cont=1;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cont==1){
			return "Ping Exitoso!!!!!!!";
		}
		else {
			return "Ping Fallido";
		}
	}
}