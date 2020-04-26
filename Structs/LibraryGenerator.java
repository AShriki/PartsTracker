package PartsBox.Structs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;


public class LibraryGenerator {

    List<String> headers;
    List<Part> parts;
    
	private ObservableList<Part> linkedParts;
	
	private Hashtable<String,Part> partHash;
	
	private String hashKey = "Manufacturer Part Number";
	
    int numHeaders;
    int numParts;

    public LibraryGenerator(){

        parts = new ArrayList<Part>();
        headers = new ArrayList<String>();

        partHash = new Hashtable<String,Part>();

        linkedParts = FXCollections.observableArrayList();
        
        headers.add(hashKey);
        headers.add("Description");
        headers.add("Quantity");
        headers.add("Unit Price");
        headers.add("Manufacturer");
        headers.add("Part Catatgory");
        headers.add("Location");
        
        numHeaders = headers.size();
        numParts = 0;

    }
    public LibraryGenerator(LibraryGenerator lib){

        parts = lib.getPartsList();
        headers = lib.getHeaderList();

        partHash = lib.getPartHashTable();

        linkedParts = FXCollections.observableArrayList(parts);
        
        numHeaders = lib.getNumHeaders();
        numParts = lib.getNumParts();

    }

	private List<String> parseItems(List<File> files,TableView<Part> table){

		ArrayList<String> unmatchedHeaders = new ArrayList<String>(this.getHeaderList());

		for(File file : files){
	    	try {
	            FileInputStream fStream = new FileInputStream(file);
	            BufferedReader in = new BufferedReader(new InputStreamReader(fStream));

	    		ArrayList<Integer> infoLocationIndex = new ArrayList<Integer>();
	    		
	    		if(infoLocationIndex.size() > getNumHeaders())
	    			break;
	            
	            //check for empty header header
	            String tempString = in.readLine();
	            if(tempString.isEmpty()){
	            	System.out.println("File has no contents or a blank first line, skipping file");
	            	break;
	            }

    			ArrayList<String> headerFromFile = new ArrayList<String>();
    			ArrayList<Integer> headerFromFileElementIndex = new ArrayList<Integer>();
    			int h = 0;
    			for(String s : tempString.replaceAll("\"", "").split(",")) { //add headers found in file to array list and index them with a second array list
    				headerFromFile.add(s);
    				headerFromFileElementIndex.add(h++);
    			}
    			
    			double stringMatchPercentage = 0;
    			
	    		for(int i = 0; i < unmatchedHeaders.size() ; i++){	// finds which headers are in which columns and find the columns that contain information of interest
    				
	    			
    				int matchedIndex = 0;
	    			boolean matchFound = false;
	    			stringMatchPercentage = 0;
	    			double tempStringMatchPercentage = 0;
	    			
	    			for(int j = 0; j < headerFromFile.size(); j++) {

	    				String a [] = headerFromFile.get(j).split(" ");
	    				String b [] = unmatchedHeaders.get(i).split(" ");
	    				
	    				tempStringMatchPercentage = similarString(a,b);
	    				
	    				if(tempStringMatchPercentage > 0.67){// string match condition

	    					if(tempStringMatchPercentage > stringMatchPercentage) {
		    					matchedIndex = j;
		    					stringMatchPercentage = tempStringMatchPercentage;						    							
	    						
	    					}
	    					matchFound = true;
	    					if(stringMatchPercentage >= 0.99) {
	    						break;
	    					}
	    				}
	    			}
	    			if(matchFound) { // remove the matched entry;
	    				infoLocationIndex.add(headerFromFileElementIndex.get(matchedIndex));
	    				headerFromFileElementIndex.remove(matchedIndex);
	    				headerFromFile.remove(matchedIndex);
	    			}
	    		}

	            //add items and attributes
	            tempString = in.readLine();
	            while (tempString != null && !tempString.isEmpty()) {

	            	String[] itemLine = new String[0];

	    			if(tempString.contains("\",\"")) {
	    				itemLine = tempString.replaceAll("\",\"", "\t").replaceAll("\"", "").split("\t");
	    			}else {
	    				itemLine = tempString.split(",");
	    			}
	            	
	            	Part p = new Part((ArrayList<String>)this.getHeaderList());

	            	for(int k = 0; k < infoLocationIndex.size() ; k++ ){
	            		p.setDetail(itemLine[infoLocationIndex.get(k)], k);
	            	}
	            	
		            addPart(p);

	            	tempString = in.readLine();
	            }
	            in.close();

	        } catch (IOException e) {
	            System.out.println("File input error: Function - parseItems()");
	        }

	    }
		this.syncPartsLists();
		table.refresh();
		return unmatchedHeaders;
	}

	public void addFiles(List<File> files, TableView<Part> table){
		parseItems(files,table);

	}

	public void addPart(Part p){
		if(!this.headers.contains(hashKey) || !this.headers.contains("Quantity")) {
			return;
		}
		if(this.partHash.containsKey(p.getDetail(this.getHeaderIndex(this.hashKey)))) {
			this.partHash.get(p.getDetail(this.getHeaderIndex(this.hashKey))).addQuantity(p.getDetail(this.getHeaderIndex("Quantity")),this.getHeaderIndex("Quantity"));
    	}
    	else {
    		this.partHash.put(p.getDetail(this.getHeaderIndex(this.hashKey)), p);
        	this.parts.add(p);
    		this.linkedParts.add(p);
    		this.numParts++;
    	}
	}

	public void removePart(int index){
		if(this.numParts > 0 && index < this.numParts){
			this.partHash.remove(parts.get(index).getDetail(this.getHeaderIndex(this.hashKey)), this.parts.get(index));
			this.parts.remove(index);
			this.linkedParts.remove(index);
			this.numParts--;
		}else {
			System.out.println("Index out of Bounds in removePart(" + index + ")");
		}
	}

	public void removePart(Part p){
		if(this.parts.contains(p)){
			this.partHash.remove(p.getDetail(this.getHeaderIndex(hashKey)),p);
			this.parts.remove(p);
			this.linkedParts.remove(p);
			this.numParts--;
		}
	}

	public void removeAll(){
		if(this.numParts > 0){
			this.parts.clear();
			this.linkedParts.clear();
			this.partHash.clear();
			this.numParts = 0;
		}
	}

	public void save(){
		//TODO write to file
	}

	public void editPart(int partNum, String header, String replacementVal){
		this.parts.get(partNum).setDetail(replacementVal, getHeaderIndex(header));
		this.linkedParts.get(partNum).setDetail(replacementVal, getHeaderIndex(header));
	}

	public int searchDetail(String header, String search){

		int headerIndex = getHeaderIndex(header);

		for(int i = 0; i < numParts; i++){
			if(this.parts.get(i).getDetail(headerIndex).equalsIgnoreCase(search))
				return i;
		}

		return -1;
	}
	public String getHeaderString() {
		String s = new String();
		int i = 0;
		for(String hdr : this.headers) {

			if( i < (this.getNumHeaders()) && i > 0) {
				s+=",";
			}

			s += hdr;

			i++;
		}

		return s;
	}
	public List<String> getHeaderList(){
		return this.headers;
	}

	public String getHeader(int index){
		if(index < 0 || (index > (numHeaders -1))) {
			
		}else {
			System.out.println("Index out of Bounds in removePart(" + index + ")");
		}
		return this.headers.get(index);
	}

	public int getHeaderIndex(String s){

		for(int i = 0; i < this.numHeaders; i++){
			if(this.headers.get(i).equalsIgnoreCase(s))
				return i;
		}

		return -1;

	}

	public int getNumHeaders(){
		return this.numHeaders;
	}

	public int getNumParts(){
		return this.numParts;
	}

	public List<Part> getPartsList(){
		return new ArrayList<Part>(this.parts);
	}

	public ObservableList<Part> getPartsObservableList(){
		return this.linkedParts;
	}
	
	public ObservableList<String> getHeaderObservableList(){
		return FXCollections.observableArrayList(this.headers);
	}

	public Part getPart(int index){
		if(index < this.numParts && index >= 0)
			return this.parts.get(index);
		else {
			System.out.println("Index out of Bounds in getPart(" + index + ") , number of parts: " + this.numParts );
		}
		return new Part((ArrayList<String>) this.getHeaderList());
	}

	private static double similarString(String a[], String b[]) { //returns the number of similarities between two strings
		HashSet<String> set1 = new HashSet<String>();
		HashSet<String> set2 = new HashSet<String>();

		int divisor = (a.length >= b.length) ? a.length : b.length;

		for(int i = 0; i < a.length; i++)
		{
			set1.add(a[i]);
		}
		for(int i = 0; i < b.length; i++)
		{
			set2.add(b[i]);
		}
		set1.retainAll(set2);
		String[] res = set1.toArray(new String[0]);

		return res.length/((double)divisor);

	}

	public void addHeader(String s){

		for(String h : this.headers){
			if(h.equalsIgnoreCase(s)){
				return;
			}
		}
	
		this.headers.add(s);
		for(Part p : this.parts){
			p.appendEmptyDetail();
		}
		for(Part p : this.linkedParts){
			p.appendEmptyDetail();
		}
		this.numHeaders++;

	}
	
	public void removeHeader(String s) {
		int index = this.getHeaderIndex(s);
		removeHeader(index);
	}
	public void removeHeader(int index) {
		if(index == -1 || index > this.numHeaders) {
			System.out.println("Index out of Bounds in removeHeader(" + index + ")");
			return;
		}
		
		for(Part p : this.parts){
			p.deleteDetail(index);
		}
		for(Part p : this.linkedParts) {
			p.deleteDetail(index);
		}
		
		this.headers.remove(index);
		this.numHeaders--;
	}
	
	public void replaceHeader(String oldHeader, String newHeader) {
		int headerIndex = this.getHeaderIndex(oldHeader);
		if(headerIndex == -1) {
			return;
		}
		
		this.headers.add(headerIndex, newHeader);
		this.headers.remove(headerIndex+1);
	}
	
	public void search(String s, String header){	// searching for a part list will alter the linked parts list to reflect the search results
		ArrayList<Part> searchResults = new ArrayList<Part>();
		
		int hdrNum = this.getHeaderIndex(header);
		
		s = s.toLowerCase();
		for(Part p : this.parts) {
			if(p.getDetail(hdrNum).toLowerCase().contains(s)) {
				searchResults.add(p);
			}
		}
		linkedParts.clear();
		linkedParts.addAll(FXCollections.observableArrayList(searchResults));
	}
	
	public Hashtable<String,Part> getPartHashTable(){
		return new Hashtable<String,Part>(this.partHash);
	}
	public void syncPartsLists() {
        linkedParts.clear();
        linkedParts.addAll(FXCollections.observableArrayList(parts));
	}
	
	public void restore(LibraryGenerator lib) {
		this.parts = lib.getPartsList();
		this.headers = lib.getHeaderList();
		linkedParts.clear();
		linkedParts.addAll(lib.getPartsObservableList());
		this.partHash = lib.getPartHashTable();
	}
}

