package Clases;

import java.util.ArrayList;
import Filtros.*;

public class Secretaria {
	private  String nombre, apellido, usuario, clave;
	private int DNI;
	private ArrayList<ParMedicoTurnos> servicios;
	
	public Secretaria(String nombre, String apellido, String usuario, String clave, int DNI) {
		this.nombre = nombre;
		this.apellido = apellido;
		this.usuario = usuario;
		this.clave = clave;
		this.DNI = DNI;
		this.servicios = new ArrayList<ParMedicoTurnos>();
	}
	
	//Aniade el m�dico s�lo si no lo ten�a
	public void addMedico(Medico m) {
		if (m != null && ! servicios.contains(m)) {
			servicios.add(new ParMedicoTurnos(m)); //Para a�adirlo lo hace en el par que tendr� sus turnos
		}
	}
	
	//S�lo aniade el turno si hay un par creado para el m�dico al cual corresponde el turno
	//Este m�todo s�lo sirve para probar las funcionalidades del primer sprint
	public void addTurno(Medico m, Turno t) {
		if (m != null && t != null) {
			int i = servicios.indexOf(new ParMedicoTurnos(m));//Busca el par correspondiente
			if (i > -1) {//Si existe el par agrega el turno
				this.servicios.get(i).addTurno(t);
			}
		}
	}
	
	//Busca y devuelve una lista de los pares m�dico-turnos con los m�dicos y turnos que cumplan con los filtros dados
	public ArrayList<ParMedicoTurnos> buscarParesMedicoTurnos(FiltroMedico fm, FiltroTurno ft) {
		ArrayList<ParMedicoTurnos> resultado_filtrado = new ArrayList<ParMedicoTurnos>();
		for (ParMedicoTurnos par: servicios) {
			Medico medico = par.getMedico();//Para cada par obtiene el m�dico
			if (fm == null || fm.cumple(medico)) {
				//Si el m�dico cumple con el filtro, genera un nuevo par para a�adir a la lista resultado
				ParMedicoTurnos p = new ParMedicoTurnos(medico);
				ArrayList<Turno> turnos = par.getTurnos();//Obtiene la lista original de turnos
				for (Turno turno: turnos)
					if ((ft == null || ft.cumple(turno)) && turno.getPaciente() == null)
						p.addTurno(turno);//Para cada turno de la lista, si cumple lo agrega al nuevo par(el filtrado)
				resultado_filtrado.add(p);//Agrega el nuevo par a la lista resultado
			}
		}
		//Devuelve la lista resultado
		return resultado_filtrado;//Podr�a estar vac�a si ning�n m�dico cumple con el filtro o estar lena de pares sin turnos si ninguno cumple los filtros
	}
	
	//Aniade la jornada al m�dico
	public void cargarDiasYHorarios(JornadaDiaria jornada, int pos_medico) {
		//No aniade null y s�lo aniade una jornada si el m�dico se encuentra cargado (si la posici�n es v�lida)
		if (jornada != null && pos_medico >= 0 && pos_medico < servicios.size())
			servicios.get(pos_medico).getMedico().addJornada(jornada);	
	}
	
	//Devuelve la lista de pares m�dico-turnos
	public ArrayList<ParMedicoTurnos> listarPares() {
		return servicios;
	}
	
	//Devuelve una lista con los m�dicos cargados
	public ArrayList<Medico> listarMedicos() {
		ArrayList<Medico> resultado = new ArrayList<Medico>();
		for (ParMedicoTurnos par: servicios)
			resultado.add(par.getMedico());
		return resultado;
	}
	
	public ArrayList<Turno> getTurnosMedico(Medico medico) { //metodo que devuelve los turnos de un medico en particular del ArrayList de pares
		int i = 0;
		while (i < servicios.size()) {
			if (servicios.get(i).getMedico().equals(medico)) {
				return servicios.get(i).getTurnos();
			}
			else
				i++;
		}
		return null; //si el medico no se encuentra retorna null
	}
	
	//Carga el turno s�lo si el m�dico estaba cargado, si tiene coherencia con la jornada del mismo y si no se superpone con otro turno de este 
	public void cargarTurnosMedico(Turno turno, int pos_medico) {
		//Comprueba que el la pos del m�dico sea v�lida
		if (turno != null && pos_medico >= 0 && pos_medico < servicios.size()) {
			ParMedicoTurnos par = servicios.get(pos_medico);
			//Comprueba que no se cruce con otro turno y que sea asignable a la jornada del m�dico
			if (!par.poseeTurnoQueCruza(turno) && par.getMedico().turnoAsignable(turno))
				par.addTurno(turno);
		}
	}
	
	//Cancela el turno s�lo si es v�lido (si las posiciones para el mismo y para su m�dico son v�lidas)
	public void cancelarTurno(int eleccion_medico, int eleccion_turno) {
		if (eleccion_medico >= 0 && eleccion_medico < servicios.size()) {
			ArrayList<Turno> turnos = servicios.get(eleccion_medico).getTurnos();
			if (eleccion_turno >= 0 && eleccion_turno < turnos.size())
				turnos.remove(eleccion_turno);
		}
	}
}
