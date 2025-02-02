import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.HashMap;
import java.time.LocalDate;

public class ASADetailed {
    public static void main(String[] args) throws FileNotFoundException {
        HashMap <String, Member> memberData = new HashMap<>();

        Scanner reader = new Scanner(new File ("lastAttendedEvent.csv"));
        reader.nextLine();

        /*
         *  Finding last meeting where member checked in,
         *  then determining if meeting is recent enough
         */

        while (reader.hasNextLine()){
            String [] line = reader.nextLine().split(",");

            //  adding member to hashmap to track statistics
            if (memberData.get(line[2]) == null){
                Member member = new Member();
                member.email = line[2];
                member.name = line[0];
                member.lastName = line[1];

                memberData.put(line[2], member);
            }

            //  checking date for validity
            if (!line[14].equals("\"\"")){
                String [] stringDate = line[14].substring(1, line[14].indexOf(" ")).split("/");
                LocalDate date = LocalDate.of(Integer.parseInt(stringDate[2]), Integer.parseInt(stringDate[0]), Integer.parseInt(stringDate[1]));

                if (
                    date.compareTo(LocalDate.of(2024, 8, 26)) > 0 
                    && (memberData.get(line[2]).lastAttendedEvent == null || date.compareTo(memberData.get(line[2]).lastAttendedEvent) > 0)
                ){
                    memberData.get(line[2]).lastAttendedEvent = date;
                    memberData.get(line[2]).eventsAttended++;
                }                
            }
        }

        /*
         *  Adding members to a new CSV file 
         *  if they have attended 3 or more meetings
         */

        PrintWriter pWriter = new PrintWriter(new File("members.csv"));
        pWriter.println("First Name,Last Name,Last Attended Meeting,Events Attended");

        for (String email : memberData.keySet()){
            if (memberData.get(email).lastAttendedEvent != null && memberData.get(email).eventsAttended >= 3){
                pWriter.println
                (
                    memberData.get(email).name + "," + 
                    memberData.get(email).lastName +","+  
                    memberData.get(email).lastAttendedEvent + "," + 
                    memberData.get(email).eventsAttended 
                );
            }
        }

        pWriter.close();
        reader.close();
    }
}
