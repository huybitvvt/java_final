CREATE TABLE IF NOT EXISTS Sach (
    MaSach INT AUTO_INCREMENT PRIMARY KEY,
    TenSach VARCHAR(200) NOT NULL,
    TacGia VARCHAR(100),
    NhaXuatBan VARCHAR(100),
    TheLoai VARCHAR(50),
    NamXuatBan INT,
    GiaBia DECIMAL(18, 2) NOT NULL DEFAULT 0,
    SoLuongTon INT NOT NULL DEFAULT 0,
    MucDatHangLai INT DEFAULT 10,
    MoTa CLOB,
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    NgayCapNhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS KhachHang (
    MaKhachHang INT AUTO_INCREMENT PRIMARY KEY,
    HoTen VARCHAR(100) NOT NULL,
    GioiTinh VARCHAR(10),
    NgaySinh DATE,
    SoDienThoai VARCHAR(20),
    Email VARCHAR(100),
    DiaChi VARCHAR(200),
    DiemTichLuy INT DEFAULT 0,
    HangThanhVien VARCHAR(30),
    TongChiTieu DECIMAL(18, 2) DEFAULT 0,
    NgayDangKy TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    NgayCapNhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS NhanVien (
    MaNhanVien INT AUTO_INCREMENT PRIMARY KEY,
    HoTen VARCHAR(100) NOT NULL,
    GioiTinh VARCHAR(10),
    NgaySinh DATE,
    SoDienThoai VARCHAR(20),
    Email VARCHAR(100),
    DiaChi VARCHAR(200),
    ChucVu VARCHAR(50) DEFAULT 'Nhân viên',
    Luong DECIMAL(18, 2) DEFAULT 0,
    NgayVaoLam DATE,
    TrangThai VARCHAR(20) DEFAULT 'Hoạt động',
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    NgayCapNhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS NhaCungCap (
    MaNhaCungCap INT AUTO_INCREMENT PRIMARY KEY,
    TenNhaCungCap VARCHAR(200) NOT NULL,
    DiaChi VARCHAR(200),
    SoDienThoai VARCHAR(20),
    Email VARCHAR(100),
    NguoiLienHe VARCHAR(100),
    MoTa CLOB,
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    NgayCapNhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS TaiKhoan (
    MaTaiKhoan INT AUTO_INCREMENT PRIMARY KEY,
    TenDangNhap VARCHAR(50) NOT NULL UNIQUE,
    MatKhau VARCHAR(100) NOT NULL,
    VaiTro VARCHAR(20) DEFAULT 'Nhân viên',
    MaNhanVien INT,
    TrangThai VARCHAR(20) DEFAULT 'Hoạt động',
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    NgayCapNhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_taikhoan_nhanvien
        FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);

CREATE TABLE IF NOT EXISTS HoaDon (
    MaHoaDon INT AUTO_INCREMENT PRIMARY KEY,
    MaHoaDonString VARCHAR(20) NOT NULL UNIQUE,
    MaKhachHang INT NULL,
    MaNhanVien INT NOT NULL,
    NgayLap TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    TongTien DECIMAL(18, 2) DEFAULT 0,
    GiamGia DECIMAL(18, 2) DEFAULT 0,
    ThanhToan DECIMAL(18, 2) DEFAULT 0,
    PhuongThucThanhToan VARCHAR(50) DEFAULT 'Tiền mặt',
    GhiChu VARCHAR(500),
    TrangThai VARCHAR(30) DEFAULT 'Đã thanh toán',
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    NgayCapNhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hoadon_khachhang
        FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
    CONSTRAINT fk_hoadon_nhanvien
        FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);

CREATE TABLE IF NOT EXISTS ChiTietHoaDon (
    MaChiTiet INT AUTO_INCREMENT PRIMARY KEY,
    MaHoaDon INT NOT NULL,
    MaSach INT NOT NULL,
    SoLuong INT NOT NULL DEFAULT 1,
    DonGia DECIMAL(18, 2) NOT NULL,
    ThanhTien DECIMAL(18, 2) NOT NULL,
    GiamGia DECIMAL(5, 2) DEFAULT 0,
    CONSTRAINT fk_cthoadon_hoadon
        FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon) ON DELETE CASCADE,
    CONSTRAINT fk_cthoadon_sach
        FOREIGN KEY (MaSach) REFERENCES Sach(MaSach)
);

CREATE TABLE IF NOT EXISTS DonNhapHang (
    MaDonNhap INT AUTO_INCREMENT PRIMARY KEY,
    MaDonNhapString VARCHAR(20) NOT NULL UNIQUE,
    MaNhaCungCap INT NOT NULL,
    MaNhanVien INT NOT NULL,
    NgayNhap TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    TongTien DECIMAL(18, 2) DEFAULT 0,
    GhiChu VARCHAR(500),
    TrangThai VARCHAR(30) DEFAULT 'Đã nhập',
    NgayTao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    NgayCapNhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_donnhap_ncc
        FOREIGN KEY (MaNhaCungCap) REFERENCES NhaCungCap(MaNhaCungCap),
    CONSTRAINT fk_donnhap_nhanvien
        FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);

CREATE TABLE IF NOT EXISTS ChiTietDonNhap (
    MaChiTiet INT AUTO_INCREMENT PRIMARY KEY,
    MaDonNhap INT NOT NULL,
    MaSach INT NOT NULL,
    SoLuong INT NOT NULL DEFAULT 1,
    DonGia DECIMAL(18, 2) NOT NULL,
    ThanhTien DECIMAL(18, 2) NOT NULL,
    CONSTRAINT fk_ctdonnhap_donnhap
        FOREIGN KEY (MaDonNhap) REFERENCES DonNhapHang(MaDonNhap) ON DELETE CASCADE,
    CONSTRAINT fk_ctdonnhap_sach
        FOREIGN KEY (MaSach) REFERENCES Sach(MaSach)
);

MERGE INTO NhanVien (MaNhanVien, HoTen, GioiTinh, NgaySinh, SoDienThoai, Email, DiaChi, ChucVu, Luong, NgayVaoLam, TrangThai)
KEY (MaNhanVien)
VALUES
    (1, 'Nguyễn Văn A', 'Nam', DATE '1990-01-01', '0912345678', 'nva@bookstore.com', 'Hà Nội', 'Admin', 15000000, DATE '2020-01-01', 'Hoạt động'),
    (2, 'Trần Thị B', 'Nữ', DATE '1995-05-15', '0923456789', 'ttb@bookstore.com', 'Hà Nội', 'Nhân viên', 8000000, DATE '2021-06-01', 'Hoạt động'),
    (3, 'Lê Văn C', 'Nam', DATE '1992-08-20', '0934567890', 'lvc@bookstore.com', 'TP.HCM', 'Nhân viên', 8000000, DATE '2021-09-01', 'Hoạt động');

MERGE INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, VaiTro, MaNhanVien, TrangThai)
KEY (MaTaiKhoan)
VALUES
    (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 'Admin', 1, 'Hoạt động'),
    (2, 'nhanvien', 'e10adc3949ba59abbe56e057f20f883e', 'Nhân viên', 2, 'Hoạt động');

MERGE INTO KhachHang (MaKhachHang, HoTen, GioiTinh, NgaySinh, SoDienThoai, Email, DiaChi, DiemTichLuy, HangThanhVien, TongChiTieu)
KEY (MaKhachHang)
VALUES
    (1, 'Phạm Thị D', 'Nữ', DATE '1998-03-10', '0945678901', 'ptd@email.com', 'Hà Nội', 100, 'Silver', 1800000),
    (2, 'Hoàng Văn E', 'Nam', DATE '1995-11-25', '0956789012', 'hve@email.com', 'TP.HCM', 50, 'Standard', 720000),
    (3, 'Ngô Thị F', 'Nữ', DATE '2000-07-18', '0967890123', 'ntf@email.com', 'Đà Nẵng', 0, 'Standard', 0);

MERGE INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, DiaChi, SoDienThoai, Email, NguoiLienHe)
KEY (MaNhaCungCap)
VALUES
    (1, 'Nhà xuất bản Trẻ', 'TP.HCM', '02838222222', 'nxbtre@nxbtre.vn', 'Nguyễn Văn X'),
    (2, 'Nhà xuất bản Giáo dục', 'Hà Nội', '02438221111', 'nxbgd@nxbgd.vn', 'Trần Thị Y'),
    (3, 'Nhà xuất bản Kim Đồng', 'TP.HCM', '02838303330', 'nxbkd@nxbkd.vn', 'Lê Văn Z');

MERGE INTO Sach (MaSach, TenSach, TacGia, NhaXuatBan, TheLoai, NamXuatBan, GiaBia, SoLuongTon, MucDatHangLai, MoTa)
KEY (MaSach)
VALUES
    (1, 'Lập trình Java cơ bản', 'Nguyễn Văn A', 'Nhà xuất bản Trẻ', 'Công nghệ', 2023, 89000, 100, 10, 'Sách dành cho người mới học Java'),
    (2, 'Python cho người mới bắt đầu', 'Trần Văn B', 'Nhà xuất bản Giáo dục', 'Công nghệ', 2023, 79000, 150, 10, 'Học Python từ cơ bản đến nâng cao'),
    (3, 'Tiếng Anh giao tiếp', 'Lê Thị C', 'Nhà xuất bản Đại học Quốc gia', 'Ngoại ngữ', 2022, 65000, 200, 10, 'Tiếng Anh thực hành cho người đi làm'),
    (4, 'Toán học cao cấp tập 1', 'Phạm Văn D', 'Nhà xuất bản Giáo dục', 'Giáo khoa', 2021, 120000, 80, 10, 'Sách giáo trình đại học'),
    (5, 'Lịch sử Việt Nam', 'Ngô Văn E', 'Nhà xuất bản Kim Đồng', 'Lịch sử', 2020, 95000, 50, 10, 'Tổng quan lịch sử Việt Nam'),
    (6, 'Kinh doanh thời 4.0', 'Hoàng Thị F', 'Nhà xuất bản Tài chính', 'Kinh tế', 2023, 150000, 60, 10, 'Chiến lược kinh doanh hiện đại'),
    (7, 'Thiết kế đồ họa', 'Vũ Văn G', 'Nhà xuất bản Mỹ thuật', 'Mỹ thuật', 2022, 180000, 40, 10, 'Học thiết kế đồ họa với Photoshop'),
    (8, 'Nấu ăn ngon mỗi ngày', 'Đặng Thị H', 'Nhà xuất bản Ẩm thực', 'Ẩm thực', 2023, 75000, 120, 10, 'Công thức nấu ăn gia đình');

MERGE INTO HoaDon (MaHoaDon, MaHoaDonString, MaKhachHang, MaNhanVien, NgayLap, TongTien, GiamGia, ThanhToan, PhuongThucThanhToan, GhiChu, TrangThai)
KEY (MaHoaDon)
VALUES
    (1, 'HD00001', 1, 2, TIMESTAMP '2026-03-08 10:15:00', 168000, 10, 151200, 'Tiền mặt', 'Đơn mẫu', 'Đã thanh toán'),
    (2, 'HD00002', 2, 2, TIMESTAMP '2026-03-09 14:20:00', 185000, 5, 175750, 'Chuyển khoản', 'Đơn mẫu', 'Đã thanh toán'),
    (3, 'HD00003', NULL, 3, TIMESTAMP '2026-03-10 09:05:00', 150000, 0, 150000, 'Tiền mặt', 'Khách lẻ', 'Đã thanh toán');

MERGE INTO ChiTietHoaDon (MaChiTiet, MaHoaDon, MaSach, SoLuong, DonGia, ThanhTien, GiamGia)
KEY (MaChiTiet)
VALUES
    (1, 1, 1, 1, 89000, 89000, 0),
    (2, 1, 3, 1, 65000, 65000, 0),
    (3, 1, 8, 1, 14000, 14000, 0),
    (4, 2, 2, 1, 79000, 79000, 0),
    (5, 2, 5, 1, 95000, 95000, 0),
    (6, 3, 6, 1, 150000, 150000, 0);

CREATE INDEX IF NOT EXISTS idx_sach_ten ON Sach(TenSach);
CREATE INDEX IF NOT EXISTS idx_sach_theloai ON Sach(TheLoai);
CREATE INDEX IF NOT EXISTS idx_khachhang_ten ON KhachHang(HoTen);
CREATE INDEX IF NOT EXISTS idx_hoadon_ngay ON HoaDon(NgayLap);
CREATE INDEX IF NOT EXISTS idx_hoadon_makh ON HoaDon(MaKhachHang);
