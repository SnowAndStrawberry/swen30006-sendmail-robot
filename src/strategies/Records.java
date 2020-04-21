package strategies;
import automail.MailItem;
import automail.PriorityMailItem;
import automail.Robot;
public class Records {
	private static int normal_number = 0;
	private static int normal_weight = 0;
	private static int overdrive_number =0;
	private static int overdrive_weight = 0;
	
    public static void normal_number_count(MailItem mailitem) {
	    normal_number ++;
    }
    
    public static void normal_weight_count(MailItem mailitem) {
	    normal_weight += mailitem.getWeight();
    }
    
    public static void overdrive_number_count(MailItem mailitem) {
    	overdrive_number ++;
    }
    
    public static void overdrive_weight_count(MailItem mailitem) {
    	overdrive_weight += mailitem.getWeight();
    }

    public static int get_normal_number() {
    	return normal_number;
    }
    
    public static int get_normal_weight() {
    	return normal_weight;
    }
    
    public static int get_overdrive_number() {
    	return overdrive_number;
    }
    
    public static int get_overdrive_weight() {
    	return overdrive_weight;
    }
}
