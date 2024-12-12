import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

class Barang {
    protected String kodeBarang;
    protected String namaBarang;
    protected double hargaBarang;
    protected String noFaktur;
    protected int jumlahBarang;
    protected String namaKasir;

    public Barang(String kodeBarang, String namaBarang, double hargaBarang, String noFaktur, int jumlahBarang, String namaKasir) {
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
        this.hargaBarang = hargaBarang;
        this.noFaktur = noFaktur;
        this.jumlahBarang = jumlahBarang;
        this.namaKasir = namaKasir;
    }

    public double hitungTotal() {
        return hargaBarang * jumlahBarang;
    }
}

class Transaksi extends Barang {
    public Transaksi(String kodeBarang, String namaBarang, double hargaBarang, String noFaktur, int jumlahBarang, String namaKasir) {
        super(kodeBarang, namaBarang, hargaBarang, noFaktur, jumlahBarang, namaKasir);
    }

    public void tampilkanDetail() {
        double total = hitungTotal();
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        System.out.println("\n+----------------------------------------------------+");
        System.out.println("Selamat Datang di Dexter Supermarket");
        System.out.println("Tanggal dan Waktu : " + formatter.format(date));
        System.out.println("+----------------------------------------------------+");
        System.out.println("No. Faktur      : " + noFaktur);
        System.out.println("Kode Barang     : " + kodeBarang);
        System.out.println("Nama Barang     : " + namaBarang);
        System.out.println("Harga Barang    : Rp " + hargaBarang);
        System.out.println("Jumlah Barang   : " + jumlahBarang);
        System.out.println("TOTAL           : Rp " + total);
        System.out.println("+----------------------------------------------------+");
        System.out.println("Kasir           : " + namaKasir);
        System.out.println("+----------------------------------------------------+");
    }
}

public class ProgramFaktur {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/supermarket";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "akashiharuchiyo";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("+-----------------------------------------------------+");
            System.out.print("Username : ");
            String username = scanner.nextLine();

            System.out.print("Password : ");
            String password = scanner.nextLine();

            String captcha = "12345";
            System.out.print("Captcha  : ");
            String userCaptcha = scanner.nextLine();

            if (!username.equals("Khun") || !password.equals("AgueroAgnes") || !userCaptcha.equals(captcha)) {
                System.out.println("Login gagal, silakan ulangi.");
                return;
            }

            System.out.println("Login berhasil!");
            System.out.println("+-----------------------------------------------------+");

            while (true) {
                System.out.println("Pilih operasi: ");
                System.out.println("1. Tambah Barang");
                System.out.println("2. Lihat Barang");
                System.out.println("3. Tambah Transaksi");
                System.out.println("4. Keluar");
                System.out.print("Pilihan: ");
                int pilihan = scanner.nextInt();
                scanner.nextLine();

                switch (pilihan) {
                    case 1:
                        tambahBarang(scanner, connection);
                        break;
                    case 2:
                        lihatBarang(connection);
                        break;
                    case 3:
                        tambahTransaksi(scanner, connection);
                        break;
                    case 4:
                        System.out.println("Keluar dari program.");
                        return;
                    default:
                        System.out.println("Pilihan tidak valid.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error koneksi database: " + e.getMessage());
        }
    }

    private static void tambahBarang(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Masukkan Kode Barang: ");
        String kodeBarang = scanner.nextLine();

        System.out.print("Masukkan Nama Barang: ");
        String namaBarang = scanner.nextLine();

        System.out.print("Masukkan Harga Barang: ");
        double hargaBarang = scanner.nextDouble();

        System.out.print("Masukkan No Faktur: ");
        String noFaktur = scanner.next();

        System.out.print("Masukkan Jumlah Barang: ");
        int jumlahBarang = scanner.nextInt();

        scanner.nextLine(); // Clear buffer

        System.out.print("Masukkan Nama Kasir: ");
        String namaKasir = scanner.nextLine();

        String sql = "INSERT INTO barang (kode_barang, nama_barang, harga_barang, no_faktur, jumlah_barang, nama_kasir) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, kodeBarang);
            preparedStatement.setString(2, namaBarang);
            preparedStatement.setDouble(3, hargaBarang);
            preparedStatement.setString(4, noFaktur);
            preparedStatement.setInt(5, jumlahBarang);
            preparedStatement.setString(6, namaKasir);
            preparedStatement.executeUpdate();
            System.out.println("Barang berhasil ditambahkan.");
        }
    }

    private static void lihatBarang(Connection connection) throws SQLException {
        String sql = "SELECT * FROM barang";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("\n+-----------------------------------------------------------+");
            System.out.println("Kode Barang | Nama Barang         | Harga Barang | No Faktur | Jumlah Barang | Nama Kasir");
            System.out.println("+-----------------------------------------------------------+");

            while (resultSet.next()) {
                String kodeBarang = resultSet.getString("kode_barang");
                String namaBarang = resultSet.getString("nama_barang");
                double hargaBarang = resultSet.getDouble("harga_barang");
                String noFaktur = resultSet.getString("no_faktur");
                int jumlahBarang = resultSet.getInt("jumlah_barang");
                String namaKasir = resultSet.getString("nama_kasir");
                System.out.printf("%-12s | %-18s | Rp %-10.2f | %-10s | %-14d | %-10s\n", kodeBarang, namaBarang, hargaBarang, noFaktur, jumlahBarang, namaKasir);
            }

            System.out.println("+-----------------------------------------------------------+");
        }
    }

    private static void tambahTransaksi(Scanner scanner, Connection connection) throws SQLException {
        System.out.print("Masukkan No Faktur: ");
        String noFaktur = scanner.nextLine();

        System.out.print("Masukkan Kode Barang: ");
        String kodeBarang = scanner.nextLine();

        System.out.print("Masukkan Jumlah Beli: ");
        int jumlahBarang = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Masukkan Nama Kasir: ");
        String namaKasir = scanner.nextLine();

        String sqlBarang = "SELECT nama_barang, harga_barang FROM barang WHERE kode_barang = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlBarang)) {
            preparedStatement.setString(1, kodeBarang);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String namaBarang = resultSet.getString("nama_barang");
                double hargaBarang = resultSet.getDouble("harga_barang");
                double totalHarga = hargaBarang * jumlahBarang;

                String sqlTransaksi = "INSERT INTO transaksi (no_faktur, kode_barang, nama_barang, harga_barang, jumlah_beli, total_harga, nama_kasir, tanggal_waktu) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
                try (PreparedStatement psTransaksi = connection.prepareStatement(sqlTransaksi)) {
                    psTransaksi.setString(1, noFaktur);
                    psTransaksi.setString(2, kodeBarang);
                    psTransaksi.setString(3, namaBarang);
                    psTransaksi.setDouble(4, hargaBarang);
                    psTransaksi.setInt(5, jumlahBarang);
                    psTransaksi.setDouble(6, totalHarga);
                    psTransaksi.setString(7, namaKasir);
                    psTransaksi.executeUpdate();

                    System.out.println("Transaksi berhasil ditambahkan.");
                }
            } else {
                System.out.println("Barang dengan kode tersebut tidak ditemukan.");
            }
        }
    }
}
