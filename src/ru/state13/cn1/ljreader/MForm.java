package ru.state13.cn1.ljreader;

import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;

public class MForm extends Form {
	
	Container customTitleArea,customCommandsArea, leftArea;
	Label customTitle,fakeTitle;
	
	public MForm(){
		setCustomTitleArea("123");
	}
	
	public void setCustomTitle(String title){
		
		if (getCustomTitleArea()==null){
			setCustomTitleArea(title);
		}else{
			this.getCustomTitle().setText(title);
		}
		this.putClientProperty("CustomTitle", customTitle);
		this.putClientProperty("CustomTitleArea", customTitleArea);
	}
	
	public Label getCustomTitle(){
		//return (Label) LJreader.getComponentByName(this.getCustomTitleArea(), "CustomTitle");
		return (Label)this.getClientProperty("CustomTitle");
	}
	
	@Override
	public Container getTitleArea(){
		//return (Label) LJreader.getComponentByName(this.getCustomTitleArea(), "CustomTitle");
		return leftArea;
	}
	
	@Override
	public Label getTitleComponent(){
		return fakeTitle;
	}
	
	public void setCustomTitleArea(String title){
		super.removeAll();
		
		if(customTitleArea!=null){
			this.setLayout(new BorderLayout());
			this.addComponent(BorderLayout.NORTH, customTitleArea);
			customTitle.setText(title);
			//System.out.println("Adding title after removeAll");
		}else{
			this.setLayout(new BorderLayout());
			customTitleArea =  new Container(new BorderLayout());
			leftArea =  new Container(new BorderLayout());
			fakeTitle=new Label();
			leftArea.addComponent(BorderLayout.CENTER, fakeTitle);
			customTitleArea.setUIID("TitleArea");
			
			customTitleArea.putClientProperty("CustomID", "CustomTitleArea");
			customCommandsArea = new Container(new BoxLayout(BoxLayout.X_AXIS));
			customCommandsArea.putClientProperty("CustomID", "customCommandsArea");
			customTitleArea.addComponent(BorderLayout.EAST, customCommandsArea);
			customTitleArea.addComponent(BorderLayout.WEST, leftArea);
			
			customTitle = new Label();
			customTitle.setText(title);
			customTitle.setUIID("Title");
//			Button b = new Button("xcv");
//			customCommands.addComponent(b);
//			Button b2 = new Button("xcv");
//			customCommands.addComponent(b2);
			customTitle.putClientProperty("CustomID", "CustomTitle");
			customTitleArea.addComponent(BorderLayout.CENTER, customTitle);
			this.putClientProperty("CustomTitle", customTitle);
			this.putClientProperty("CustomTitleArea", customTitleArea);
			this.putClientProperty("customCommandsArea", customCommandsArea);
			this.addComponent(BorderLayout.NORTH, customTitleArea);
		}
	}
	
	public Container getCustomCommandsArea(){
		//return (Container) LJreader.getComponentByName(this, "customCommandsArea");
		return (Container)this.getClientProperty("customCommandsArea");
	}

	public Container getCustomTitleArea(){
		//return (Container) LJreader.getComponentByName(this, "CustomTitleArea");
		return (Container)this.getClientProperty("CustomTitleArea");
	}
	
	@Override
	public void removeAll(){
		super.removeAll();
		setCustomTitleArea("");
	}
	
	public void addCustomCommand(Command cmd){
		Button b = new Button();
		b.setCommand(cmd);
		b.setIcon(cmd.getIcon());
		b.setUIID("TitleCommand");
		b.setText("");
//		System.out.println("******");
//		Container c =this.getCustomCommands();
//		System.out.println(c);
//		System.out.println("******");
		this.getCustomCommandsArea().addComponent(b);
		getCustomTitleArea().setPreferredH(cmd.getIcon().getHeight()+2*b.getStyle().getMargin(TOP));
	}
	
	public void removeAllCustomCommands(){
		this.getCustomCommandsArea().removeAll();
		this.putClientProperty("customCommandsArea", customCommandsArea);
	}
	
}
