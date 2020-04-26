package PartsBox.Structs;

import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

public class Part {

	private ArrayList<String> partDetails;

	public Part(ArrayList<String> headers){
		partDetails = new ArrayList<String>();

        for(int i = 0; i < headers.size(); i++){
            partDetails.add("");
        }

	}

	public void appendEmptyDetail() {
		partDetails.add("");
	}
	public void appendDetail(String s) {
		partDetails.add(s);
	}
	public String getDetail(int n){

		if(n < (partDetails.size()) && (n > -1)){
			return partDetails.get(n);
		}
		return "Invalid Query";
	}
	
	public void deleteDetail(int index) {
		if(!partDetails.isEmpty() && (index > partDetails.size() || index < 0)) {
			partDetails.remove(index);
		}
	}
	
	public ObservableValue<String> getObsAttribute(int n){
		if(n < (partDetails.size()-1) && (n > -1)){
			return new SimpleStringProperty(partDetails.get(n));
		}

        return new SimpleStringProperty("Invalid Query");
	}

	public void setDetail(String s, int n){

		if(n < 0 || n >= partDetails.size()){
			System.out.println("Error: Out of bounds in setDetail(string, int) in Part.java");
			return;
		}
		partDetails.set(n, s);

	}

	public void addQuantity(String addend,int index){
		int n = 0;
		try {
			n = Integer.parseInt(addend);
		}catch(NumberFormatException ne) {
			System.out.println("Required a number, got: " + addend);
			return;
		}
		int sum = Integer.parseInt(partDetails.get(index)) + n;
		this.setDetail(Integer.toString(sum) , index);
	}
	public void subQuantity(String subtrahend,int index){
		int n = 0;
		
		try {
			n = Integer.parseInt(subtrahend);
		}catch(NumberFormatException ne) {
			System.out.println("Required a number, got: " + subtrahend);
			return;
		}
		int sum = Integer.parseInt(partDetails.get(index)) - n;
		this.setDetail(Integer.toString(sum) , index);
	}

}


