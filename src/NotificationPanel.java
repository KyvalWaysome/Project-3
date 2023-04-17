

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class NotificationPanel extends JFrame{

    private JPanel p = new JPanel();
    private JLabel messageLabel = new JLabel("You Have Overdue Task(s)");

    public NotificationPanel() 
    {
        setTitle("Overdue Task(s)");
        setResizable(false);
        setSize(200, 100);

        p.add(messageLabel);

        getContentPane().add(p);

        setLocationRelativeTo(null);

        //below deals with checking if there is  task that is overdue

        Date Date = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        String Time = sdf.format(Date); //current system time

        for(Tasks t : Tasks.ArrofTasks)
        {
            try
            {
                if(sdf.parse(Time).after(sdf.parse(t.getEndTime())))
                {
                    playSound("mixkit-interface-option-select-2573.wav");
                    setVisible(true);
                }

            }
            catch(ParseException e){System.out.println(e.getStackTrace());}            
        }
        
    }

    private void playSound(String soundFilePath) {
        try 
        {
            File soundfile = new File(soundFilePath);
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(soundfile));
            clip.start();
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }
}