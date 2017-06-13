package it.polito.tdp.formulaone.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.formulaone.model.Circuit;
import it.polito.tdp.formulaone.model.Constructor;
import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.Season;


public class FormulaOneDAO {

	public List<Integer> getAllYearsOfRace() {
		
		String sql = "SELECT year FROM races ORDER BY year" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Integer> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(rs.getInt("year"));
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Season> getAllSeasons() {
		
		String sql = "SELECT year, url FROM seasons ORDER BY year" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Season> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(new Season(Year.of(rs.getInt("year")), rs.getString("url"))) ;
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Circuit> getAllCircuits() {

		String sql = "SELECT circuitId, name FROM circuits ORDER BY name";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Circuit> list = new ArrayList<>();
			while (rs.next()) {
				list.add(new Circuit(rs.getInt("circuitId"), rs.getString("name")));
			}

			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Constructor> getAllConstructors() {

		String sql = "SELECT constructorId, name FROM constructors ORDER BY name";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Constructor> constructors = new ArrayList<>();
			while (rs.next()) {
				constructors.add(new Constructor(rs.getInt("constructorId"), rs.getString("name")));
			}

			conn.close();
			return constructors;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List <Driver> getDriversForSeason (Season s){
		String sql = " SELECT DISTINCT drivers.* " +
						"FROM races, results, drivers " +
						"WHERE races.year = ? " +
						"AND results.raceId = races.raceId " +
						"AND results.position is not null " +         // controllo che il campo non sia NULL
						"AND results.driverId = drivers.driverId";
		
		Connection conn =  DBConnect.getConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, s.getYear().getValue());  // voglio una variabile di tipo INTERO, che corrisonde alla variabile di tipo YEAR
			
			ResultSet res = st.executeQuery();
			
			List<Driver> result = new ArrayList<>();
			
			while( res.next()){
				int id = res.getInt("driverId");
				String ref = res.getString("driverref");
				int number = res.getInt("number");
				String code = res.getString("code");
				String nome = res.getString("forename");
				String cognome = res.getString("surname");
				LocalDate data = res.getDate("dob").toLocalDate();
				String nazionalita = res.getString("nationality");
				String url = res.getString("url");
			
				Driver d = new Driver (id,ref,number,code,nome,cognome,data,nazionalita,url);
				
				result.add(d);
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
	}

	/**
	 * Conta il numero di vittorie di d1 su d2 nella stagione s
	 * @param d1
	 * @param d2
	 * @param s
	 * @return
	 */
	public Integer contaVittorie (Driver d1, Driver d2, Season s){
		
		String sql = "SELECT count(races.raceId) as count " + 
				"FROM results r1, results r2, races " +  
				"WHERE races.year = ? "+
				"AND races.raceId = r1.raceId " + 
				"AND r1.raceId = r2.raceId " + 
				"AND r1.position < r2.position " + 
				"AND r1.driverId = ? " + 
				"AND r2.driverId = ?" ;
		
		Connection conn = DBConnect.getConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, s.getYear().getValue());
			st.setInt(2, d1.getDriverId());
			st.setInt(3, d2.getDriverId());
			
			ResultSet res = st.executeQuery();
			res.next(); // mi posiziono sulla prima riga
						// non ce bisogno che metta un if o while perche la query restituisce SEMPRE un intero
						// anche in caso di nessun risultato (restituisce 0)
			
			Integer result = res.getInt("count");
		
			conn.close();
			return result;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	public static void main(String[] args) {
		FormulaOneDAO dao = new FormulaOneDAO() ;
		
		List<Integer> years = dao.getAllYearsOfRace() ;
		System.out.println(years);
		
		List<Season> seasons = dao.getAllSeasons() ;
		System.out.println(seasons);

		
		List<Circuit> circuits = dao.getAllCircuits();
		System.out.println(circuits);

		List<Constructor> constructors = dao.getAllConstructors();
		System.out.println(constructors);
		
	}
	
}
