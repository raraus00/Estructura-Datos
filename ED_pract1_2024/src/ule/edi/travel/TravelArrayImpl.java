package ule.edi.travel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ule.edi.model.*;

public class TravelArrayImpl implements Travel {

	private static final Double DEFAULT_PRICE = 100.0;
	private static final Byte DEFAULT_DISCOUNT = 25;
	private static final Byte CHILDREN_EXMAX_AGE = 18;
	private Date travelDate;
	private int nSeats;

	private Double price; // precio de entradas
	private Byte discountAdvanceSale; // descuento en venta anticipada (0..100)

	private Seat[] seats;

	public TravelArrayImpl(Date date, int nSeats) {
		price = DEFAULT_PRICE;
		discountAdvanceSale = DEFAULT_DISCOUNT;

		travelDate = date;
		this.nSeats = nSeats;

		seats = new Seat[nSeats];
	}

	public TravelArrayImpl(Date date, int nSeats, Double price, Byte discount) {
		travelDate = date;
		this.nSeats = nSeats;
		this.price = price;
		discountAdvanceSale = discount;

		seats = new Seat[nSeats];
	}

	@Override
	public Byte getDiscountAdvanceSale() {
		return discountAdvanceSale;
	}

	@Override
	public int getNumberOfSoldSeats() {
		int count = 0;
		for (Seat seat : seats) {
			if (seat != null) {
				count++;
			}
		}

		return count;
	}

	@Override
	public int getNumberOfNormalSaleSeats() {
		int count = 0;
		for (Seat seat : seats) {
			if (seat != null && !seat.getAdvanceSale()) {
				count++;
			}
		}

		return count;
	}

	@Override
	public int getNumberOfAdvanceSaleSeats() {
		int count = 0;
		for (Seat seat : seats) {
			if (seat != null && seat.getAdvanceSale()) {
				count++;
			}
		}

		return count;
	}

	@Override
	public int getNumberOfSeats() {
		return seats.length;
	}

	@Override
	public int getNumberOfAvailableSeats() {
		int count = 0;
		for (Seat seat : seats) {
			if (seat == null) {
				count++;
			}
		}

		return count;
	}

	@Override
	public Seat getSeat(int pos) {
		if (pos <= 0 || pos > seats.length) {
			return null;
		}
		
		return seats[pos - 1];
	}

	@Override
	public Person refundSeat(int pos) {
		if (pos <= 0 || pos > seats.length || seats[pos - 1] == null) {
			return null;
		}

		Person person = seats[pos - 1].getHolder();
		seats[pos - 1] = null;

		return person;
	}

	private boolean isChildren(int age) {
		return age < CHILDREN_EXMAX_AGE;
	}

	private boolean isAdult(int age) {
		return age >= CHILDREN_EXMAX_AGE;
	}

	@Override
	public List<Integer> getAvailableSeatsList() {
		List<Integer> list = new ArrayList<Integer>(nSeats);

		for (int i = 0; i < seats.length; i++) {
			if (seats[i] == null) {
				list.add(i + 1);
			}
		}

		return list;
	}

	@Override
	public List<Integer> getAdvanceSaleSeatsList() {
		List<Integer> list = new ArrayList<Integer>(nSeats);

		for (int i = 0; i < seats.length; i++) {
			if (seats[i] != null && seats[i].getAdvanceSale()) {
				list.add(i + 1);
			}
		}

		return list;
	}

	@Override
	public int getMaxNumberConsecutiveSeats() {
		int count = 0;
		int maxCount = 0;
		
		
		for (int i = 0; i < seats.length; i++) {
			if (seats[i] != null) {
				if (count > maxCount) {
					maxCount = count;
				}
				
				count = 0;
				continue;
			}
			
			count++;
		}

		return maxCount > count ? maxCount : count;
	}

	@Override
	public boolean isAdvanceSale(Person p) {
		for (Seat seat : seats) {
			if (seat != null && seat.getAdvanceSale() && seat.getHolder().equals(p)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Date getTravelDate() {
		return travelDate;
	}

	@Override
	public boolean sellSeatPos(int pos, String nif, String name, int edad, boolean isAdvanceSale) {
		if (pos <= 0 || pos > seats.length || getPosPerson(nif) != -1 || seats[pos - 1] != null) {
			return false;
		}

		Person person = new Person(nif, name, edad);
		seats[pos - 1] = new Seat(isAdvanceSale, person);

		return true;
	}

	@Override
	public int getNumberOfChildren() {
		int count = 0;
		for (Seat seat : seats) {
			if (seat != null && isChildren(seat.getHolder().getAge())) {
				count++;
			}
		}

		return count;
	}

	@Override
	public int getNumberOfAdults() {
		int count = 0;
		for (Seat seat : seats) {
			if (seat != null && isAdult(seat.getHolder().getAge())) {
				count++;
			}
		}

		return count;
	}

	@Override
	public Double getCollectionTravel() {
		double earned = 0;

		for (Seat seat : seats) {
			if (seat == null) {
				continue;
			}

			earned += price - (seat.getAdvanceSale() ? price * discountAdvanceSale/100 : 0);
		}

		return earned;
	}

	@Override
	public int getPosPerson(String nif) {
		for (int i = 0; i < seats.length; i++) {
			if (seats[i] != null && seats[i].getHolder().getNif().equals(nif)) {
				return i + 1;
			}
		}

		return -1;
	}

	@Override
	public int sellSeatFrontPos(String nif, String name, int edad, boolean isAdvanceSale) {
		if (getPosPerson(nif) != -1) {
			return -1;
		}
		
		for (int i = 0; i < seats.length; i++) {
			if (seats[i] == null) {
				Person person = new Person(nif, name, edad);
				seats[i] = new Seat(isAdvanceSale, person);

				return i + 1;
			}
		}

		return -1;
	}

	@Override
	public int sellSeatRearPos(String nif, String name, int edad, boolean isAdvanceSale) {
		if (getPosPerson(nif) != -1) {
			return -1;
		}
		
		for (int i = seats.length - 1; i >= 0; i--) {
			if (seats[i] == null) {
				Person person = new Person(nif, name, edad);
				seats[i] = new Seat(isAdvanceSale, person);

				return i + 1;
			}
		}

		return -1;
	}

	@Override
	public Double getSeatPrice(Seat seat) {
		return price - (seat.getAdvanceSale() ? price * discountAdvanceSale/100 : 0);
	}

	@Override
	public double getPrice() {
		return this.price;
	}

}