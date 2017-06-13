package it.polito.tdp.formulaone.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {
	
	private FormulaOneDAO dao;
	private List <Season> seasons;
	private SimpleDirectedWeightedGraph <Driver,DefaultWeightedEdge > grafo;
	// variabili di stato della ricorsione
	private int tassoMin;
	private List <Driver> best;
	
	public Model (){
		this.dao = new FormulaOneDAO();
	}
		
	public List <Season> getSeasons(){
		if(this.seasons == null){
			this.seasons = dao.getAllSeasons();
		}
		return seasons;
	}

	
	public void creaGrafo(Season s){
		this.grafo = new SimpleDirectedWeightedGraph <> (DefaultWeightedEdge.class);
		
		// aggiungo i vertici
		List <Driver> drivers = dao.getDriversForSeason(s);
		Graphs.addAllVertices(this.grafo, drivers);
		
		// aggiungo gli archi
		for(Driver d1 : this.grafo.vertexSet()){
			for(Driver d2 : this.grafo.vertexSet()){
				if(! d1.equals(d2)) {
					Integer vittorie = dao.contaVittorie(d1, d2, s);
					if( vittorie >0){
						Graphs.addEdgeWithVertices(this.grafo, d1, d2, vittorie);
						// inserisce in ordine il grafo, il source, il target, il peso
					}
				}
			}
		}
	}
	
	public Driver getbestDriver(){
		Driver best = null ;
		int max = Integer.MIN_VALUE;
		
		for(Driver d : this.grafo.vertexSet()){
			int peso =0;
			
			for( DefaultWeightedEdge e : grafo.outgoingEdgesOf(d)){
				peso += grafo.getEdgeWeight(e);
			}
			
			for( DefaultWeightedEdge e : grafo.incomingEdgesOf(d)){
				peso -= grafo.getEdgeWeight(e);
			}
			
			if(peso > max){
				max = peso;
				best = d;
			}
		}
		return best;
	}
	
	
	public List<Driver> getDreamTeam (int k){
		
		Set<Driver> parziale = new HashSet <Driver>();
		this.tassoMin = Integer.MAX_VALUE;
		this.best = null;
		recursive(0, parziale,k);
		return best;
	}
	
	
	// In ingresso ricevo il @teamParziale composto da @step elementi.
	// La variabile @step parte da 0.
	// Il caso terminale e` quando @step = @k, ed in quel caso va calcolato il tasso di sconfitta
	// Altrimenti si procede ricorsivamente ad aggiungere un nuovo vertice ( il step+1 esimo)
	// scegliendolo tra i vertici non ancora presenti nel team
	
	public void recursive (int step, Set <Driver> parziale, int k){
		
		// condizione di terminazione
		if( step == k){
			
			// calcolare tasso di sconfitta del team
			int tasso = this.tassoSconfitta(parziale);
			
			// eventualmente aggiornare il minimo
			if (tasso < tassoMin){
				tassoMin = tasso;
				best = new ArrayList<>(parziale);
					
			}
		} else{
			// caso normale, devo aggiungere drivers al team parziale
			Set <Driver> candidati = new HashSet<Driver>(grafo.vertexSet());
			candidati.removeAll(parziale);
			for( Driver d : candidati){
				parziale.add(d);
				recursive( step+1, parziale, k);
				parziale.remove(d);
			}
		}
	}
			
	private int tassoSconfitta (Set <Driver> parziale){
		int tasso = 0;
		for( DefaultWeightedEdge e : this.grafo.edgeSet()){
			if( ! parziale.contains(grafo.getEdgeSource(e)) && parziale.contains(grafo.getEdgeTarget(e))) {
				tasso += grafo.getEdgeWeight(e);
			}
		}  
		return tasso;
	}
	
	// alternativa un po piu veloce
	private int tassoSconfitta2 (Set <Driver> parziale){
		int tasso = 0;
		for( Driver driver : parziale){
			for( DefaultWeightedEdge e : grafo.incomingEdgesOf(driver)){
			tasso += grafo.getEdgeWeight(e);
			}
		}
		return tasso;
	}
	
}
