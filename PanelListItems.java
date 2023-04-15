import javax.swing.*;

import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class PanelListItems extends JPanel{

    private JButton manipulateData, sortTimeTaken, sortTaskCompleted, TaskChecker;

    private JCheckBox notifications;

    public static JProgressBar progressBar;

    public static DefaultTableModel model = new DefaultTableModel();

    public static JTable table;

    private JScrollPane scrollPane;

    private JPanel tablePanel = new JPanel(), buttonPanel = new JPanel(),/**taskedPanel = new JPanel(),*/notiPanel = new JPanel();
    
    public static int totalTask,totalTaskComplete;  

    public PanelListItems()
    {
        super(null); //establishes a new layout for the GUI to use

        setBounds(0,0,650,700);

        String[] columnNames=  {"First Name",
                "Last Name", 
                "Task Outline",
                "Start Time",
                "Estimated Duration (Mins)",
                "End Time",
                "Completed"};

        model=new DefaultTableModel(columnNames,0);
        table = new JTable(model);
        loadNames("names.txt");
        loadTasks("tasks.txt");
        showTable(); //list of data items to be put in the table in the main panel

        table.setPreferredScrollableViewportSize(new Dimension(500, Tasks.ArrofTasks.size()*15 +50));
        table.setFillsViewportHeight(true);
        tablePanel.setLayout(new GridLayout());
        scrollPane = new JScrollPane(table);

        tablePanel.add(scrollPane);

        //the above deals with list of data items in a table


        manipulateData = new JButton("Manipulate Data");
        TaskChecker = new JButton("Check a Task");
        sortTimeTaken = new JButton("Sort by Time Needed to Complete");
        sortTaskCompleted = new JButton("Sort by Completed Tasks");

        manipulateData.addActionListener(new ManipulateDataListener());
        TaskChecker.addActionListener(new TaskCheckerListener());
        sortTimeTaken.addActionListener(new SortTimeTakenListener());
        sortTaskCompleted.addActionListener(new SortTaskCompletedListener());
        buttonPanel.setLayout(new GridLayout(2,2));
        buttonPanel.add(manipulateData);
        buttonPanel.add(TaskChecker);
        buttonPanel.add(sortTimeTaken);
        buttonPanel.add(sortTaskCompleted);


        notifications = new JCheckBox("Enable Notifications"); //"Enable Notifications for Overdue Tasks"

        notifications.addActionListener(new NotificationsListener());
        notiPanel.setLayout(new GridLayout());
        notiPanel.add(notifications);
        
        //taskedPanel.add(nameDropDown);
        progressBar = new JProgressBar();
        progressBar.setValue(0); //probably redundant due to the fill method below
        progressBar.setStringPainted(true);
        fill();

        add(progressBar);
        tablePanel.setBounds(0,0, 650, 500);
        buttonPanel.setBounds(0,500,650,100);
        notiPanel.setBounds(0,600,225,100);
        progressBar.setBounds(150,620, 350, 25);
        //taskedPanel.setBounds(225,600,425,100);

        add(tablePanel);
        add(buttonPanel);
        add(notiPanel);

        //fill(Tasks.ratioOfTasksCompleted());
        

    }

    // function to dynamically increase progress  
    public static void fill()
    {  
        for (Person t:Tasks.ArrofNames){
            totalTask+=t.getEstTaskTimeLeft() +t.getTaskComplete();
            totalTaskComplete += t.getTaskComplete();
        }
        progressBar.setMaximum(totalTask);
        progressBar.setValue(totalTaskComplete); 
        /**try{  
            while(i <= completes){  
                // fill the menu bar to the defined value using   
                // the setValue( ) method  
                progressBar.setValue(i) ;  
   
                // delay the thread by 100 ms  
                Thread.sleep(100);  
                // increasing the progress every time by 1%  
                i += 1 ;  //i++
            }  
        }  
        catch (Exception e) {  
          System.out.println(e);    
        }
        **/  
    }  

    private void fill(int ratio)  //for overall progress bar
    {  
        try{  
            progressBar.setValue(ratio) ;  
        }  
        catch (Exception e) {  
          System.out.println(e);    
        }  
    }  



    public void loadNames(String nfile){
        try{
            Scanner nscan = null;
            nscan = new Scanner(new File(nfile));
            while(nscan.hasNext()){
                String[] nextLine = nscan.nextLine().split(" ");
                String name = nextLine[0] + " " + nextLine[1];
                int taskComplete = Integer.parseInt(nextLine[2]);
                int estTaskTimeLeft = Integer.parseInt(nextLine[3]);
                Person p = new Person(name,taskComplete,estTaskTimeLeft);
                Tasks.ArrofNames.add(p);
            }
            nscan.close();
        }
        catch(IOException ioe){}
    }

    public void loadTasks(String tfile){
        Scanner tscan = null;
        try{
            tscan = new Scanner(new File(tfile));
            while (tscan.hasNext()){
                String[] nextLine = tscan.nextLine().split(",");
                String name = nextLine[0];
                String taskName = nextLine[1];
                String startTime = nextLine[2];
                int estFin = Integer.parseInt(nextLine[3]);
                boolean completed = Boolean.parseBoolean(nextLine[4]);
                Tasks t = new Tasks(name,taskName,estFin,completed);
                t.setStartTime(LocalTime.parse(startTime).truncatedTo(ChronoUnit.MINUTES));
                t.setEndTime();
                Tasks.ArrofTasks.add(t);
                new PopUpPanel(t.getName(), t.getTaskOutline(), t.getEndTime());
            }
            tscan.close();
        }
        catch (IOException e) {}
    }

    public static void saveTasks(String tfile){

        try{
            File dataSaver = new File(tfile);
            PrintWriter saveTo = new PrintWriter(dataSaver);
            for (Tasks t : Tasks.ArrofTasks){
                saveTo.println(t.getName()+","+t.getTaskOutline()+","+t.getStartTime()+ "," +t.getExpectedTime() + "," + t.getCompleted());
            }
            saveTo.close();
        }
        catch (IOException e) {}

    }

    public static void saveNames(String nfile){
        try{
            File nameSaver = new File(nfile);
            PrintWriter saveToName = new PrintWriter(nameSaver);
            for (Person t : Tasks.ArrofNames){
                saveToName.println(t.getName() + " " +t.getTaskComplete() + " " + t.getEstTaskTimeLeft());
            }
            saveToName.close();
        }
        catch (IOException e) {}
    }

    public static void showTable() //adds all existing persons', that are in the text file, tasks to the table
    {
        for(Person person : Tasks.ArrofNames)
        {
            addToTable(person.getName());
        }
    }

    private static void addToTable(String person) //adds a person's task to the table if they have a task attached to them in the text file
    {
        for(Tasks s : Tasks.ArrofTasks)
        {
            if(s.getName().matches(person))
            {
                String[] name = s.getName().split(" ");

                String complete = "No";

                if(s.getCompleted() == true){
                    complete = "Yes";
                }

                String[] item={name[0], name[1], s.getTaskOutline(), s.getStartTime(), "" + s.getExpectedTime(), s.getEndTime(), complete};
                model.addRow(item);   
            }

        }

             

    }

    //add functionality to these actionlisteners

    private class ManipulateDataListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            ManipulateDataPanel test = new ManipulateDataPanel();
        }
    }

    private class TaskCheckerListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            TaskCheckerPanel test = new TaskCheckerPanel();
        }
    }

    private class SortTimeTakenListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            SortTimePanel random = new SortTimePanel();
        }
    }

    private class SortTaskCompletedListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            SortTaskCompletedPanel random = new SortTaskCompletedPanel();
        }
    }

    private class NotificationsListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            NotificationPanel random = new NotificationPanel();
        }
    }
/** 
    public void itemStateChanged(ItemEvent e) {
        ArrayList<Tasks> numofTask = new ArrayList<>(); 
        ArrayList<Tasks> numCompleted = new ArrayList<>();
        if (e.getSource() == nameDropDownPub){
            for (Tasks t: Tasks.ArrofTasks){
                if (t.getName().equals((String) nameDropDownPub.getSelectedItem())) {
                    numofTask.add(t);
                    if (t.getCompleted() == true){
                        numCompleted.add(t);
                    }
                }
            }
            fill(numCompleted.size(),numofTask.size());
        }
            numofTask.clear();
            numCompleted.clear();
    }
*/
}
