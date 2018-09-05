package app.view;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import app.Main;
import app.model.Rule;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

public class MainViewController {
	public static ArrayList<Rule> rules = new ArrayList<Rule>();
	
	@FXML
	public TextField wordTextField;
	
	@FXML
	public TextField ruleLeftTextField;
	@FXML
	public TextField ruleRightTextField;
	@FXML
	public Button addRuleButton;
	@FXML
	public ListView<String> ruleListView;
	
	@FXML
	public Button ruleUpButton;
	@FXML
	public Button ruleDownButton;
	@FXML
	public Button ruleSaveButton;
	@FXML
	public Button ruleLoadButton;
	
	@FXML
	public void handleLoad() throws IOException{
		FileChooser fc = new FileChooser();
		fc.setTitle("������� ���� � ���������");
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		File tempFile = fc.showOpenDialog(Main.inst.stage);
		if (tempFile != null){
			
			rules.clear();
			ruleListView.getItems().clear();
			
			BufferedReader br = new BufferedReader(new FileReader(tempFile));
			String tempString;
			StringBuilder tempFrom = new StringBuilder(), tempTo = new StringBuilder();
			boolean l;
			char tempChar;
			
			while((tempString = br.readLine()) != null){
				tempFrom.delete(0, tempFrom.length());
				tempTo.delete(0, tempTo.length());
				l = true;
				
				for(int i = 0; i < tempString.length(); i++){
					tempChar = tempString.charAt(i);
					if (tempChar == '=' || tempChar == '>'){l = false;continue;}

					if (l)tempFrom.append(tempChar);else tempTo.append(tempChar);
				}
				
				addRule(tempFrom.toString(), tempTo.toString());
				
			}
			br.close();
		}
	}
	
	@FXML
	public void handleSave() throws IOException{
	
		FileChooser fc = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		fc.getExtensionFilters().add(extFilter);
		fc.setTitle("������� ���� �������� �������");
		File tempFile = fc.showSaveDialog(Main.inst.stage);
		if (tempFile == null)return;
		BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
		for(Rule rule : rules)bw.write(rule.toString()+"\n");
		bw.close();
	}
	
	@FXML
	public void handleUp(){
		swapElement(-1);
	}
	
	private void swapElement(int sigh){
		int index = ruleListView.getSelectionModel().getSelectedIndex();
		if (index == -1 || index + sigh > rules.size()-1 || index + sigh < 0)return;
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		    	ruleListView.scrollTo(index + sigh);
				ruleListView.getSelectionModel().select(index + sigh);
		    }
		});
		
		String tempString = ruleListView.getItems().get(index);
		ruleListView.getItems().remove(index);
		ruleListView.getItems().add(index + sigh, tempString);
		Rule tempRule = rules.get(index);
		rules.remove(index);
		rules.add(index, tempRule);
	}
	
	@FXML
	public void handleDown(){
		swapElement(1);
	}
	
	
	private int tempIndex;
	@FXML
	public void handleRemoveRule(MouseEvent me){
		if (me.getClickCount() == 2){
			tempIndex = ruleListView.getSelectionModel().getSelectedIndex();
			ruleListView.getItems().remove(tempIndex);
			rules.remove(tempIndex);
		}
	}
	
	@FXML
	public void handleAddRule(){
		String from = ruleLeftTextField.getText();
		String to = ruleRightTextField.getText();
		
		addRule(from, to);
	}
	
	private void addRule(String from, String to){
		for(Rule r : rules){
			if(r.getFrom().equals(from)){
				return;
			}
		}
		rules.add(new Rule(from, to));
		ruleRightTextField.clear();
		ruleLeftTextField.clear();
		ruleListView.getItems().add(rules.get(rules.size()-1).toString());
	}
	
	@FXML
	public void handleCalculate() throws IOException{
		ArrayList<String> words = new ArrayList<>();
		words.add(wordTextField.getText());
		// main loop
		m: while(true){
			
			for(Rule r : rules){
				if (r.canApply(words.get(words.size()-1))){
					System.out.println(words.get(words.size()-1));
					words.add(r.apply(words.get(words.size()-1)));
					if (r.isLast())
						break m;
					else
						continue m;
				}
			}
		
			break;
			
		}
		File f = new File(words.get(0) + ".txt");;
		try{
			f.getCanonicalFile();
			
		}
		catch(Exception e){
			f = new File("FILENAME_ERROR.txt");
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		for(String word : words){
			bw.write(word);
			bw.newLine();
		}
		bw.close();
		Desktop.getDesktop().open(f);
		
		System.out.println(words.get(words.size()-1));
			
	}
	
	
	
}
