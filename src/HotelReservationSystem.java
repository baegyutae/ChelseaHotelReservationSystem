import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

class Room {
    private String type;
    private int price;
    private int totalAvailable; // 객실의 총 개수
    private int available;      // 남은 객실 수

    public Room(String type, int price, int totalAvailable) {
        this.type = type;
        this.price = price;
        this.totalAvailable = totalAvailable;
        this.available = totalAvailable;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available > 0;
    }

    public void reserveRoom() {
        if (available > 0) {
            available--;
        }
    }

    public void releaseRoom() {
        if (available < totalAvailable) {
            available++;
        }
    }

    public int getAvailable() {
        return available;
    }
}

class Customer {
    private String name;
    private String phoneNumber;
    private int budget;

    public Customer(String name, String phoneNumber, int budget) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.budget = budget;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getBudget() {
        return budget;
    }

    public void deductBudget(int amount) {
        budget -= amount;
    }
}

class Reservation {
    private UUID id;
    private Room room;
    private Customer customer;
    private String reservationDate;

    public Reservation(Room room, Customer customer, String reservationDate) {
        this.id = UUID.randomUUID();
        this.room = room;
        this.customer = customer;
        this.reservationDate = reservationDate;
    }

    public UUID getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getReservationDate() {
        return reservationDate;
    }
}

class Hotel {
    private ArrayList<Room> rooms;
    private ArrayList<Customer> customers;
    private ArrayList<Reservation> reservations;

    public Hotel() {
        rooms = new ArrayList<>();
        customers = new ArrayList<>();
        reservations = new ArrayList<>();
        rooms.add(new Room("스위트룸", 300000, 2));
        rooms.add(new Room("더블룸", 150000, 3));
        rooms.add(new Room("싱글룸", 100000, 5));
    }

    public void displayMenu() {
        System.out.println("호텔에 오신걸 환영합니다.");
        System.out.println("1. 예약 하기");
        System.out.println("2. 예약 확인");
        System.out.println("3. 예약 취소");
        System.out.println("4. 종료");
        System.out.print("입력 : ");
    }

    public void makeReservation() {
        Scanner scanner = new Scanner(System.in);
        Room selectedRoom = null; // Room 변수를 선언하고 초기화

        System.out.println("남은 객실:");
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (room.isAvailable()) {
                System.out.println((i + 1) + ". " + room.getType() + " - 가격: " + room.getPrice() + " - 남은 객실 수: " + room.getAvailable());
            }
        }

        System.out.print("원하는 객실 번호를 선택하세요: ");
        int roomChoice = scanner.nextInt();

        if (roomChoice < 1 || roomChoice > rooms.size()) {
            System.out.println("유효하지 않은 선택입니다.");
            return;
        }

        selectedRoom = rooms.get(roomChoice - 1); // 선택한 객실을 할당

        if (!selectedRoom.isAvailable()) {
            System.out.println("선택한 객실은 모두 예약되었습니다. 다른 객실을 선택하세요.");
            return;
        }

        selectedRoom = rooms.get(roomChoice - 1);

        System.out.print("고객 성함을 입력하세요: ");
        String name = scanner.next();

        System.out.print("고객 전화번호를 입력하세요 (XXX-XXXX-XXXX 형식): ");
        String phoneNumber = scanner.next();

        String phoneRegex = "\\d{3}-\\d{4}-\\d{4}";
        if (!Pattern.matches(phoneRegex, phoneNumber)) {
            System.out.println("올바른 전화번호 형식이 아닙니다.");
            return;
        }

        System.out.print("고객 소지금을 입력하세요: ");
        int budget = scanner.nextInt();

        if (budget < selectedRoom.getPrice()) {
            System.out.println("소지금이 부족하여 예약이 불가능합니다.");
            return;
        }

        System.out.print("예약 날짜를 입력하세요 (연월일 형식, 예: 20231026): ");
        String reservationDateInput = scanner.next();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date reservationDate;
        try {
            reservationDate = dateFormat.parse(reservationDateInput);
        } catch (Exception e) {
            System.out.println("올바른 날짜 형식이 아닙니다.");
            return;
        }

        selectedRoom.reserveRoom();

        Customer customer = new Customer(name, phoneNumber, budget);
        customers.add(customer);

        Reservation reservation = new Reservation(selectedRoom, customer, reservationDateInput);
        reservations.add(reservation);

        UUID reservationId = reservation.getId();
        System.out.println("예약이 완료되었습니다. 예약 번호: " + reservationId);

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void viewReservations() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("예약 번호를 입력하세요: ");
        UUID reservationId = UUID.fromString(scanner.next());

        boolean reservationFound = false;
        for (Reservation reservation : reservations) {
            if (reservation.getId().equals(reservationId)) {
                reservationFound = true;
                System.out.println("예약이 확인되었습니다.");
                System.out.println("고객 이름: " + reservation.getCustomer().getName());
                System.out.println("객실 유형: " + reservation.getRoom().getType());
                System.out.println("예약 날짜: " + reservation.getReservationDate());
                break;
            }
        }

        if (!reservationFound) {
            System.out.println("해당 예약 번호로 예약을 찾을 수 없습니다.");
        }
    }

    public void cancelReservation(UUID reservationId) {
        Reservation reservationToCancel = null;
        for (Reservation reservation : reservations) {
            if (reservation.getId().equals(reservationId)) {
                reservationToCancel = reservation;
                break;
            }
        }

        if (reservationToCancel != null) {
            // 방 상태 변경: 다시 이용 가능하게
            reservationToCancel.getRoom().releaseRoom();

            // 고객의 소지금 복구
            int refundAmount = reservationToCancel.getRoom().getPrice();
            reservationToCancel.getCustomer().deductBudget(-refundAmount);

            // 예약 제거
            reservations.remove(reservationToCancel);
            System.out.println("예약이 취소되었습니다. 예약 번호: " + reservationId);
        } else {
            System.out.println("해당 예약 번호로 예약을 찾을 수 없습니다.");
        }
    }
}

public class HotelReservationSystem {
    public static void main(String[] args) {
        Hotel hotel = new Hotel();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            hotel.displayMenu();
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    hotel.makeReservation();
                    break;
                case 2:
                    hotel.viewReservations();
                    break;
                case 3:
                    System.out.println("예약 번호를 입력하세요: ");
                    UUID reservationId = UUID.fromString(scanner.next());
                    hotel.cancelReservation(reservationId);
                    break;
                case 4:
                    System.exit(0);
                    break;
                default:
                    System.out.println("유효하지 않은 선택입니다.");
            }
        }
    }
}
