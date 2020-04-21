package strategies;

import java.util.LinkedList;
import java.util.Comparator;
import java.util.ListIterator;

import automail.MailItem;
import automail.PriorityMailItem;
import automail.Robot;
import automail.Simulation;
import exceptions.ItemTooHeavyException;

public class MailPool implements IMailPool {

	private class Item {
		int priority;
		int destination;
		MailItem mailItem;
		// Use stable sort to keep arrival time relative positions
		
		public Item(MailItem mailItem) {
			priority = (mailItem instanceof PriorityMailItem) ? ((PriorityMailItem) mailItem).getPriorityLevel() : 1;
			destination = mailItem.getDestFloor();
			this.mailItem = mailItem;
		}
	}
	
	public class ItemComparator implements Comparator<Item> {
		@Override
		public int compare(Item i1, Item i2) {
			int order = 0;
			if (i1.priority < i2.priority) {
				order = 1;
			} else if (i1.priority > i2.priority) {
				order = -1;
			} else if (i1.destination < i2.destination) {
				order = 1;
			} else if (i1.destination > i2.destination) {
				order = -1;
			}
			return order;
		}
	}
	
	private LinkedList<Item> pool;
	private LinkedList<Robot> robots;

	public MailPool(int nrobots){
		// Start empty
		pool = new LinkedList<Item>();
		robots = new LinkedList<Robot>();
	}

	public void addToPool(MailItem mailItem) {
		Item item = new Item(mailItem);
		pool.add(item);
		pool.sort(new ItemComparator());
	}
	
	@Override
	public void step() throws ItemTooHeavyException {
		try{
			ListIterator<Robot> i = robots.listIterator();
			while (i.hasNext()) loadRobot(i);
		} catch (Exception e) { 
            throw e; 
        } 
	}
	
	private void loadRobot(ListIterator<Robot> i) throws ItemTooHeavyException {
		Robot robot = i.next();
		assert(robot.isEmpty());
		//System.out.printf("P: %3d%n", pool.size());
		ListIterator<Item> j = pool.listIterator();
		if (pool.size() > 0) {
			if (Simulation.isOVERDRIVE_ENABLED()) {
				try {
					MailItem HandItem = j.next().mailItem;
					Item ii = new Item(HandItem);
					robot.addToHand(HandItem); // hand first as we want higher priority delivered first
					j.remove();
					if(ii.priority >= 50) {
						Records.overdrive_number_count(HandItem);
						Records.overdrive_weight_count(HandItem);
						robot.dispatch("overdrive");
					}else {
						Records.normal_number_count(HandItem);
						Records.normal_weight_count(HandItem);
					    if (pool.size() > 0) {
						    MailItem TubeItem = getTubeItem(j);
						    robot.addToTube(TubeItem);
							Records.normal_number_count(TubeItem);
							Records.normal_weight_count(TubeItem);
						    j.remove();
					    }
					    robot.dispatch("normal");
					}// send the robot off if it has any items to deliver
					i.remove();       // remove from mailPool queue
				} catch (Exception e) { 
		            throw e; 
		        } 
			}else {
				try {
					MailItem HandItem = j.next().mailItem;
					robot.addToHand(HandItem); // hand first as we want higher priority delivered first
					j.remove();
					Records.normal_number_count(HandItem);
					Records.normal_weight_count(HandItem);
				    if (pool.size() > 0) {
					    MailItem TubeItem = getTubeItem(j);
					    robot.addToTube(TubeItem);
						Records.normal_number_count(TubeItem);
						Records.normal_weight_count(TubeItem);
					    j.remove();
				    }
				    robot.dispatch("normal");
				// send the robot off if it has any items to deliver
				i.remove();       // remove from mailPool queue
				} catch (Exception e) { 
		            throw e; 
		        } 
			}

		}
	}
	private MailItem getTubeItem(ListIterator<Item> j) {

		MailItem TubeItem = j.next().mailItem;
		return TubeItem;
	}
	@Override
	public void registerWaiting(Robot robot) { // assumes won't be there already
		robots.add(robot);
	}

}
