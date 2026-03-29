-- ============================================
-- FILE SQL HOÀN CHỈNH - Chạy một lần
-- Bao gồm tạo database, table và dữ liệu mẫu đầy đủ
-- ============================================

USE master;
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = 'QuanLyCuaHangSach')
BEGIN
    ALTER DATABASE QuanLyCuaHangSach SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QuanLyCuaHangSach;
END
GO

CREATE DATABASE QuanLyCuaHangSach;
GO

USE QuanLyCuaHangSach;
GO

-- Create tables
CREATE TABLE Sach (
    MaSach INT IDENTITY(1,1) PRIMARY KEY,
    TenSach NVARCHAR(200) NOT NULL,
    TacGia NVARCHAR(100),
    NhaXuatBan NVARCHAR(100),
    TheLoai NVARCHAR(50),
    NamXuatBan INT,
    GiaBia DECIMAL(18,2) NOT NULL DEFAULT 0,
    SoLuongTon INT NOT NULL DEFAULT 0,
    MoTa NVARCHAR(MAX),
    NgayTao DATETIME DEFAULT GETDATE(),
    NgayCapNhat DATETIME DEFAULT GETDATE()
);

CREATE TABLE KhachHang (
    MaKhachHang INT IDENTITY(1,1) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    GioiTinh NVARCHAR(10),
    NgaySinh DATE,
    SoDienThoai NVARCHAR(20),
    Email NVARCHAR(100),
    DiaChi NVARCHAR(200),
    DiemTichLuy INT DEFAULT 0,
    NgayDangKy DATETIME DEFAULT GETDATE(),
    NgayCapNhat DATETIME DEFAULT GETDATE()
);

CREATE TABLE NhanVien (
    MaNhanVien INT IDENTITY(1,1) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    GioiTinh NVARCHAR(10),
    NgaySinh DATE,
    SoDienThoai NVARCHAR(20),
    Email NVARCHAR(100),
    DiaChi NVARCHAR(200),
    ChucVu NVARCHAR(50) DEFAULT N'Nhân viên',
    Luong DECIMAL(18,2) DEFAULT 0,
    NgayVaoLam DATE,
    TrangThai NVARCHAR(20) DEFAULT N'Hoạt động',
    NgayTao DATETIME DEFAULT GETDATE(),
    NgayCapNhat DATETIME DEFAULT GETDATE()
);

CREATE TABLE NhaCungCap (
    MaNhaCungCap INT IDENTITY(1,1) PRIMARY KEY,
    TenNhaCungCap NVARCHAR(200) NOT NULL,
    DiaChi NVARCHAR(200),
    SoDienThoai NVARCHAR(20),
    Email NVARCHAR(100),
    NguoiLienHe NVARCHAR(100),
    MoTa NVARCHAR(MAX),
    NgayTao DATETIME DEFAULT GETDATE(),
    NgayCapNhat DATETIME DEFAULT GETDATE()
);

CREATE TABLE TaiKhoan (
    MaTaiKhoan INT IDENTITY(1,1) PRIMARY KEY,
    TenDangNhap NVARCHAR(50) NOT NULL UNIQUE,
    MatKhau NVARCHAR(100) NOT NULL,
    VaiTro NVARCHAR(20) DEFAULT N'Nhân viên',
    MaNhanVien INT,
    TrangThai NVARCHAR(20) DEFAULT N'Hoạt động',
    NgayTao DATETIME DEFAULT GETDATE(),
    NgayCapNhat DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_taikhoan_nhanvien FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);

CREATE TABLE HoaDon (
    MaHoaDon INT IDENTITY(1,1) PRIMARY KEY,
    MaHoaDonString VARCHAR(20) NOT NULL UNIQUE,
    MaKhachHang INT NULL,
    MaNhanVien INT NOT NULL,
    NgayLap DATETIME DEFAULT GETDATE(),
    TongTien DECIMAL(18,2) DEFAULT 0,
    GiamGia DECIMAL(18,2) DEFAULT 0,
    ThanhToan DECIMAL(18,2) DEFAULT 0,
    PhuongThucThanhToan NVARCHAR(50) DEFAULT N'Tiền mặt',
    GhiChu NVARCHAR(500),
    TrangThai NVARCHAR(30) DEFAULT N'Đã thanh toán',
    NgayTao DATETIME DEFAULT GETDATE(),
    NgayCapNhat DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_hoadon_khachhang FOREIGN KEY (MaKhachHang) REFERENCES KhachHang(MaKhachHang),
    CONSTRAINT fk_hoadon_nhanvien FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);

CREATE TABLE ChiTietHoaDon (
    MaChiTiet INT IDENTITY(1,1) PRIMARY KEY,
    MaHoaDon INT NOT NULL,
    MaSach INT NOT NULL,
    SoLuong INT NOT NULL DEFAULT 1,
    DonGia DECIMAL(18,2) NOT NULL,
    ThanhTien DECIMAL(18,2) NOT NULL,
    GiamGia DECIMAL(5,2) DEFAULT 0,
    CONSTRAINT fk_cthoadon_hoadon FOREIGN KEY (MaHoaDon) REFERENCES HoaDon(MaHoaDon) ON DELETE CASCADE,
    CONSTRAINT fk_cthoadon_sach FOREIGN KEY (MaSach) REFERENCES Sach(MaSach)
);

CREATE TABLE DonNhapHang (
    MaDonNhap INT IDENTITY(1,1) PRIMARY KEY,
    MaDonNhapString VARCHAR(20) NOT NULL UNIQUE,
    MaNhaCungCap INT NOT NULL,
    MaNhanVien INT NOT NULL,
    NgayNhap DATETIME DEFAULT GETDATE(),
    TongTien DECIMAL(18,2) DEFAULT 0,
    GhiChu NVARCHAR(500),
    TrangThai NVARCHAR(30) DEFAULT N'Đã nhập',
    NgayTao DATETIME DEFAULT GETDATE(),
    NgayCapNhat DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_donnhap_ncc FOREIGN KEY (MaNhaCungCap) REFERENCES NhaCungCap(MaNhaCungCap),
    CONSTRAINT fk_donnhap_nhanvien FOREIGN KEY (MaNhanVien) REFERENCES NhanVien(MaNhanVien)
);

CREATE TABLE ChiTietDonNhap (
    MaChiTiet INT IDENTITY(1,1) PRIMARY KEY,
    MaDonNhap INT NOT NULL,
    MaSach INT NOT NULL,
    SoLuong INT NOT NULL DEFAULT 1,
    DonGia DECIMAL(18,2) NOT NULL,
    ThanhTien DECIMAL(18,2) NOT NULL,
    CONSTRAINT fk_ctdonnhap_donnhap FOREIGN KEY (MaDonNhap) REFERENCES DonNhapHang(MaDonNhap) ON DELETE CASCADE,
    CONSTRAINT fk_ctdonnhap_sach FOREIGN KEY (MaSach) REFERENCES Sach(MaSach)
);
GO

-- ============================================
-- NHÂN VIÊN (20 người)
-- ============================================
SET IDENTITY_INSERT NhanVien ON;
INSERT INTO NhanVien (MaNhanVien, HoTen, GioiTinh, NgaySinh, SoDienThoai, Email, DiaChi, ChucVu, Luong, NgayVaoLam, TrangThai)
VALUES
    (1, N'Nguyễn Văn A', N'Nam', '1988-05-10', '0912345678', 'nva@bookstore.com', N'Hà Nội', N'Quản lý', 15000000, '2018-01-15', N'Hoạt động'),
    (2, N'Trần Thị B', N'Nữ', '1992-08-20', '0923456789', 'ttb@bookstore.com', N'TP.HCM', N'Nhân viên', 8000000, '2020-03-10', N'Hoạt động'),
    (3, N'Lê Văn C', N'Nam', '1990-12-05', '0934567890', 'lvc@bookstore.com', N'Hà Nội', N'Nhân viên', 8500000, '2019-06-20', N'Hoạt động'),
    (4, N'Phạm Thị D', N'Nữ', '1993-03-25', '0945678901', 'ptd@bookstore.com', N'Hà Nội', N'Quản lý', 12000000, '2020-05-15', N'Hoạt động'),
    (5, N'Hoàng Văn E', N'Nam', '1991-11-30', '0956789012', 'hve@bookstore.com', N'TP.HCM', N'Nhân viên', 7500000, '2022-01-10', N'Hoạt động'),
    (6, N'Nguyễn Thị F', N'Nữ', '1994-07-12', '0967890123', 'ntf@bookstore.com', N'Đà Nẵng', N'Nhân viên', 7200000, '2022-03-20', N'Hoạt động'),
    (7, N'Trần Văn G', N'Nam', '1996-02-28', '0978901234', 'tvg@bookstore.com', N'Hà Nội', N'Nhân viên', 7000000, '2022-06-01', N'Hoạt động'),
    (8, N'Lê Thị H', N'Nữ', '1997-09-15', '0989012345', 'lth@bookstore.com', N'TP.HCM', N'Nhân viên', 6800000, '2022-08-10', N'Hoạt động'),
    (9, N'Vũ Văn I', N'Nam', '1990-12-05', '0990123456', 'vvi@bookstore.com', N'Hải Phòng', N'Nhân viên', 8000000, '2021-04-01', N'Hoạt động'),
    (10, N'Đặng Thị J', N'Nữ', '1998-04-18', '0901234567', 'dtj@bookstore.com', N'Cần Thơ', N'Nhân viên', 6500000, '2023-01-05', N'Hoạt động'),
    (11, N'Bùi Văn K', N'Nam', '1992-06-22', '0912345670', 'bvk@bookstore.com', N'Hà Nội', N'Thu ngân', 8500000, '2021-11-15', N'Hoạt động'),
    (12, N'Ngô Thị L', N'Nữ', '1995-10-08', '0923456780', 'ntl@bookstore.com', N'TP.HCM', N'Thu ngân', 7800000, '2022-02-01', N'Hoạt động'),
    (13, N'Phạm Văn M', N'Nam', '1993-04-15', '0934567891', 'pvm@bookstore.com', N'Hà Nội', N'Nhân viên', 7000000, '2023-03-01', N'Hoạt động'),
    (14, N'Vũ Thị N', N'Nữ', '1996-07-22', '0945678902', 'vtn@bookstore.com', N'TP.HCM', N'Nhân viên', 6800000, '2023-05-15', N'Hoạt động'),
    (15, N'Đặng Văn P', N'Nam', '1994-11-08', '0956789013', 'dvp@bookstore.com', N'Đà Nẵng', N'Thu ngân', 8200000, '2022-08-20', N'Hoạt động'),
    (16, N'Bùi Thị Q', N'Nữ', '1997-02-14', '0967890124', 'btq@bookstore.com', N'Hải Phòng', N'Nhân viên', 6500000, '2023-07-01', N'Hoạt động'),
    (17, N'Hoàng Văn R', N'Nam', '1991-09-30', '0978901235', 'hvr@bookstore.com', N'Cần Thơ', N'Nhân viên', 7500000, '2021-10-10', N'Hoạt động'),
    (18, N'Ngô Thị S', N'Nữ', '1995-06-18', '0989012346', 'nts@bookstore.com', N'Hà Nội', N'Thu ngân', 8000000, '2022-04-05', N'Hoạt động'),
    (19, N'Phạm Thị T', N'Nữ', '1998-12-25', '0990123457', 'ptt@bookstore.com', N'TP.HCM', N'Nhân viên', 6200000, '2023-09-01', N'Hoạt động'),
    (20, N'Trần Văn U', N'Nam', '1992-03-12', '0901234568', 'tvu@bookstore.com', N'Đà Nẵng', N'Quản lý kho', 9500000, '2021-01-20', N'Hoạt động');
SET IDENTITY_INSERT NhanVien OFF;
GO

-- ============================================
-- KHÁCH HÀNG (100 người)
-- ============================================
SET IDENTITY_INSERT KhachHang ON;
INSERT INTO KhachHang (MaKhachHang, HoTen, GioiTinh, NgaySinh, SoDienThoai, Email, DiaChi, DiemTichLuy)
VALUES
    (1, N'Nguyễn Văn Minh', N'Nam', '1990-01-15', '0912345001', 'nvminh@email.com', N'Hà Nội', 350),
    (2, N'Trần Thị Hương', N'Nữ', '1992-06-20', '0912345002', 'tthuong@email.com', N'TP.HCM', 280),
    (3, N'Lê Văn Phong', N'Nam', '1988-11-10', '0912345003', 'lvphong@email.com', N'Hà Nội', 420),
    (4, N'Lê Văn G', N'Nam', '1993-05-22', '0978901234', 'lvg@email.com', N'Hà Nội', 200),
    (5, N'Trần Thị H', N'Nữ', '1997-09-12', '0989012345', 'tth@email.com', N'TP.HCM', 150),
    (6, N'Phạm Văn I', N'Nam', '1992-01-30', '0990123456', 'pvi@email.com', N'Hải Phòng', 75),
    (7, N'Nguyễn Thị J', N'Nữ', '1999-11-05', '0901234567', 'ntj@email.com', N'Cần Thơ', 25),
    (8, N'Vũ Văn K', N'Nam', '1994-07-18', '0912345679', 'tvk@email.com', N'Hà Nội', 180),
    (9, N'Đặng Thị L', N'Nữ', '1996-03-25', '0923456781', 'dtl@email.com', N'TP.HCM', 120),
    (10, N'Bùi Văn M', N'Nam', '1991-12-08', '0934567892', 'bvm@email.com', N'Đà Nẵng', 90),
    (11, N'Ngô Thị N', N'Nữ', '1998-06-15', '0945678903', 'ntn@email.com', N'Hà Nội', 60),
    (12, N'Hoàng Văn O', N'Nam', '1995-02-28', '0956789014', 'hvo@email.com', N'TP.HCM', 300),
    (13, N'Lê Thị P', N'Nữ', '1993-10-12', '0967890125', 'ltp@email.com', N'Hải Phòng', 45),
    (14, N'Trần Văn Q', N'Nam', '1997-04-20', '0978901236', 'tvq@email.com', N'Cần Thơ', 85),
    (15, N'Phạm Thị R', N'Nữ', '1994-08-05', '0989012347', 'ptr@email.com', N'Hà Nội', 220),
    (16, N'Nguyễn Văn S', N'Nam', '1992-12-22', '0990123458', 'nvs@email.com', N'TP.HCM', 110),
    (17, N'Trần Thị T', N'Nữ', '1999-05-10', '0901234569', 'ttt@email.com', N'Đà Nẵng', 30),
    (18, N'Lê Văn U', N'Nam', '1996-09-18', '0912345670', 'lvu@email.com', N'Hà Nội', 175),
    (19, N'Vũ Thị V', N'Nữ', '1994-01-25', '0923456782', 'vtv@email.com', N'TP.HCM', 95),
    (20, N'Đặng Văn W', N'Nam', '1991-07-30', '0934567893', 'dvw@email.com', N'Hải Phòng', 250),
    (21, N'Bùi Thị X', N'Nữ', '1998-11-12', '0945678904', 'btx@email.com', N'Cần Thơ', 55),
    (22, N'Hoàng Văn Y', N'Nam', '1993-03-08', '0956789015', 'hvy@email.com', N'Hà Nội', 140),
    (23, N'Ngô Thị Z', N'Nữ', '1997-06-22', '0967890126', 'ntz@email.com', N'TP.HCM', 70),
    (24, N'Phạm Văn AA', N'Nam', '1995-10-15', '0978901237', 'pvaa@email.com', N'Đà Nẵng', 190),
    (25, N'Nguyễn Thị AB', N'Nữ', '1992-08-28', '0989012348', 'ntab@email.com', N'Hà Nội', 210),
    (26, N'Trần Văn AC', N'Nam', '1999-02-14', '0990123459', 'tvac@email.com', N'TP.HCM', 40),
    (27, N'Lê Thị AD', N'Nữ', '1994-12-03', '0901234560', 'ltad@email.com', N'Hải Phòng', 130),
    (28, N'Vũ Văn AE', N'Nam', '1996-05-20', '0912345671', 'tvae@email.com', N'Cần Thơ', 80),
    (29, N'Đặng Thị AF', N'Nữ', '1993-09-08', '0923456783', 'dtaf@email.com', N'Hà Nội', 165),
    (30, N'Bùi Văn AG', N'Nam', '1998-04-25', '0934567894', 'bvag@email.com', N'TP.HCM', 35),
    (31, N'Hoàng Thị AH', N'Nữ', '1991-11-18', '0945678905', 'htah@email.com', N'Đà Nẵng', 280),
    (32, N'Ngô Văn AI', N'Nam', '1997-07-05', '0956789016', 'nvai@email.com', N'Hà Nội', 100),
    (33, N'Phạm Thị AJ', N'Nữ', '1995-01-12', '0967890127', 'ptaj@email.com', N'TP.HCM', 155),
    (34, N'Nguyễn Văn AK', N'Nam', '1994-06-30', '0978901238', 'nvak@email.com', N'Hải Phòng', 125),
    (35, N'Trần Thị AL', N'Nữ', '1999-08-17', '0989012349', 'ntal@email.com', N'Cần Thơ', 20),
    (36, N'Vũ Thị Mai', N'Nữ', '1997-04-22', '0990123450', 'vtmai@email.com', N'Hà Nội', 145),
    (37, N'Đặng Văn Hùng', N'Nam', '1993-08-15', '0991123451', 'dvhung@email.com', N'TP.HCM', 195),
    (38, N'Bùi Thị Lan', N'Nữ', '1996-12-08', '0992123452', 'btlan@email.com', N'Đà Nẵng', 88),
    (39, N'Hoàng Văn Nam', N'Nam', '1991-05-30', '0993123453', 'hvnam@email.com', N'Hải Phòng', 230),
    (40, N'Ngô Thị Thy', N'Nữ', '1999-09-12', '0994123454', 'ntthy@email.com', N'Cần Thơ', 42),
    (41, N'Phạm Văn Đạt', N'Nam', '1994-02-25', '0995123455', 'pvdat@email.com', N'Hà Nội', 310),
    (42, N'Trần Thị Ngọc', N'Nữ', '1995-07-18', '0996123456', 'ttngoc@email.com', N'TP.HCM', 175),
    (43, N'Lê Văn Bảo', N'Nam', '1992-10-05', '0997123457', 'lvbao@email.com', N'Đà Nẵng', 265),
    (44, N'Vũ Thị Hồng', N'Nữ', '1998-03-28', '0998123458', 'vthong@email.com', N'Hải Phòng', 78),
    (45, N'Đặng Văn Khoa', N'Nam', '1990-11-20', '0999123459', 'dvkhoa@email.com', N'Cần Thơ', 340),
    (46, N'Bùi Thị Minh', N'Nữ', '1993-06-14', '0900123460', 'btminh@email.com', N'Hà Nội', 198),
    (47, N'Hoàng Văn Sơn', N'Nam', '1997-01-08', '0901123461', 'hvson@email.com', N'TP.HCM', 125),
    (48, N'Ngô Thị Yến', N'Nữ', '1994-08-22', '0902123462', 'ntyen@email.com', N'Đà Nẵng', 255),
    (49, N'Phạm Văn Thành', N'Nam', '1991-12-30', '0903123463', 'pvthanh@email.com', N'Hải Phòng', 290),
    (50, N'Trần Thị Quỳnh', N'Nữ', '1999-05-15', '0904123464', 'ttquynh@email.com', N'Cần Thơ', 65),
    (51, N'Lê Văn Hậu', N'Nam', '1992-09-08', '0905123465', 'lvhau@email.com', N'Hà Nội', 380),
    (52, N'Vũ Thị Anh', N'Nữ', '1996-04-18', '0906123466', 'vtanh@email.com', N'TP.HCM', 142),
    (53, N'Đặng Văn Trung', N'Nam', '1993-11-25', '0907123467', 'dvtrung@email.com', N'Đà Nẵng', 215),
    (54, N'Bùi Thị Hà', N'Nữ', '1998-07-12', '0908123468', 'btha@email.com', N'Hải Phòng', 95),
    (55, N'Hoàng Văn Tuấn', N'Nam', '1990-02-05', '0909123469', 'hvtuan@email.com', N'Cần Thơ', 425),
    (56, N'Ngô Thị Linh', N'Nữ', '1995-10-22', '0910123470', 'ntlinh@email.com', N'Hà Nội', 188),
    (57, N'Phạm Văn Hải', N'Nam', '1994-06-08', '0911123471', 'pvhai@email.com', N'TP.HCM', 275),
    (58, N'Trần Văn Dũng', N'Nam', '1991-03-15', '0912123472', 'tvdung@email.com', N'Đà Nẵng', 320),
    (59, N'Lê Thị Thu', N'Nữ', '1997-12-28', '0913123473', 'ltthu@email.com', N'Hải Phòng', 112),
    (60, N'Vũ Văn Đức', N'Nam', '1992-08-10', '0914123474', 'vvduc@email.com', N'Cần Thơ', 245),
    (61, N'Đặng Thị Hoa', N'Nữ', '1999-04-05', '0915123475', 'dthoa@email.com', N'Hà Nội', 55),
    (62, N'Bùi Văn Minh', N'Nam', '1993-09-18', '0916123476', 'bvminh@email.com', N'TP.HCM', 365),
    (63, N'Hoàng Thị Ngân', N'Nữ', '1996-01-22', '0917123477', 'htngan@email.com', N'Đà Nẵng', 158),
    (64, N'Ngô Văn Lâm', N'Nam', '1991-07-30', '0918123478', 'nvlam@email.com', N'Hải Phòng', 285),
    (65, N'Phạm Thị Thảo', N'Nữ', '1998-11-12', '0919123479', 'ptthao@email.com', N'Cần Thơ', 82),
    (66, N'Trần Văn Hiếu', N'Nam', '1994-05-08', '0920123480', 'tvhieu@email.com', N'Hà Nội', 335),
    (67, N'Lê Thị My', N'Nữ', '1995-08-25', '0921123481', 'ltmy@email.com', N'TP.HCM', 195),
    (68, N'Vũ Văn Phước', N'Nam', '1992-02-14', '0922123482', 'vvphuoc@email.com', N'Đà Nẵng', 410),
    (69, N'Đặng Thị Phương', N'Nữ', '1997-06-18', '0923123483', 'dtphuong@email.com', N'Hải Phòng', 128),
    (70, N'Bùi Văn Thịnh', N'Nam', '1990-12-05', '0924123484', 'bvthinh@email.com', N'Cần Thơ', 295),
    (71, N'Hoàng Văn Đạt', N'Nam', '1993-04-22', '0925123485', 'hvdat@email.com', N'Hà Nội', 178),
    (72, N'Ngô Thị Hà', N'Nữ', '1996-09-30', '0926123486', 'ntha@email.com', N'TP.HCM', 225),
    (73, N'Phạm Văn Cường', N'Nam', '1991-11-15', '0927123487', 'pvcuong@email.com', N'Đà Nẵng', 355),
    (74, N'Trần Thị Bích', N'Nữ', '1999-03-08', '0928123488', 'ttbich@email.com', N'Hải Phòng', 72),
    (75, N'Lê Văn Thắng', N'Nam', '1994-07-20', '0929123489', 'lvthang@email.com', N'Cần Thơ', 268),
    (76, N'Vũ Thị Thủy', N'Nữ', '1995-12-12', '0930123490', 'vtthuy@email.com', N'Hà Nội', 185),
    (77, N'Đặng Văn Long', N'Nam', '1992-06-28', '0931123491', 'dvlong@email.com', N'TP.HCM', 312),
    (78, N'Bùi Thị Hương', N'Nữ', '1998-10-05', '0932123492', 'bthuong@email.com', N'Đà Nẵng', 98),
    (79, N'Hoàng Văn Bảo', N'Nam', '1990-01-18', '0933123493', 'hvbao@email.com', N'Hải Phòng', 445),
    (80, N'Ngô Thị Mai', N'Nữ', '1997-05-22', '0934123494', 'ntmai@email.com', N'Cần Thơ', 165),
    (81, N'Phạm Văn Tài', N'Nam', '1993-08-08', '0935123495', 'pvtai@email.com', N'Hà Nội', 238),
    (82, N'Trần Thị Hạnh', N'Nữ', '1996-02-28', '0936123496', 'tthanh@email.com', N'TP.HCM', 142),
    (83, N'Lê Văn Khôi', N'Nam', '1991-09-15', '0937123497', 'lvkhoi@email.com', N'Đà Nẵng', 298),
    (84, N'Vũ Thị Ngọc', N'Nữ', '1994-04-10', '0938123498', 'vtngoc@email.com', N'Hải Phòng', 175),
    (85, N'Đặng Văn Hòa', N'Nam', '1999-07-25', '0939123499', 'dvhoa@email.com', N'Cần Thơ', 48),
    (86, N'Bùi Thị Uyên', N'Nữ', '1992-11-30', '0940123500', 'btuyen@email.com', N'Hà Nội', 215),
    (87, N'Hoàng Văn Kiệt', N'Nam', '1995-03-18', '0941123501', 'hvkiet@email.com', N'TP.HCM', 328),
    (88, N'Ngô Thị Trang', N'Nữ', '1998-08-12', '0942123502', 'nttrang@email.com', N'Đà Nẵng', 85),
    (89, N'Phạm Văn Đức', N'Nam', '1990-06-05', '0943123503', 'pvduc@email.com', N'Hải Phòng', 385),
    (90, N'Trần Thị Liên', N'Nữ', '1993-12-22', '0944123504', 'ttlien@email.com', N'Cần Thơ', 158),
    (91, N'Lê Văn Phát', N'Nam', '1997-02-08', '0945123505', 'lvphat@email.com', N'Hà Nội', 198),
    (92, N'Vũ Thị Hạnh', N'Nữ', '1994-09-28', '0946123506', 'vthanh2@email.com', N'TP.HCM', 245),
    (93, N'Đặng Văn Tuấn', N'Nam', '1991-05-14', '0947123507', 'dvtuan2@email.com', N'Đà Nẵng', 275),
    (94, N'Bùi Thị Vân', N'Nữ', '1996-10-20', '0948123508', 'btvan@email.com', N'Hải Phòng', 132),
    (95, N'Hoàng Văn Nhân', N'Nam', '1992-07-08', '0949123509', 'hvnhân@email.com', N'Cần Thơ', 312),
    (96, N'Ngô Thị Kim', N'Nữ', '1999-01-25', '0950123510', 'ntkim@email.com', N'Hà Nội', 62),
    (97, N'Phạm Văn Sơn', N'Nam', '1993-04-12', '0951123511', 'pvson2@email.com', N'TP.HCM', 228),
    (98, N'Trần Thị Nga', N'Nữ', '1995-11-18', '0952123512', 'ttnga@email.com', N'Đà Nẵng', 175),
    (99, N'Lê Văn Thọ', N'Nam', '1990-08-30', '0953123513', 'lvtho@email.com', N'Hải Phòng', 398),
    (100, N'Vũ Thị Lan', N'Nữ', '1997-06-15', '0954123514', 'vtlan@email.com', N'Cần Thơ', 115);
SET IDENTITY_INSERT KhachHang OFF;
GO

-- ============================================
-- NHÀ CUNG CẤP (20 nhà cung cấp)
-- ============================================
SET IDENTITY_INSERT NhaCungCap ON;
INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, DiaChi, SoDienThoai, Email, NguoiLienHe, MoTa)
VALUES
    (1, N'Nhà xuất bản Giáo dục', N'Hà Nội', '02438221110', 'nxbd@nxbgd.vn', N'Nguyễn Văn A', N'NXB giáo dục chính thức'),
    (2, N'Nhà xuất bản Trẻ', N'TP.HCM', '02838221111', 'nxbtre@nxbtre.vn', N'Lê Thị B', N'NXB sách thiếu nhi và văn học'),
    (3, N'Nhà xuất bản Kim Đồng', N'Hà Nội', '02438221109', 'nxbkd@nxbkd.vn', N'Trần Văn C', N'NXB sách thiếu nhi hàng đầu'),
    (4, N'Nhà xuất bản Tài chính', N'Hà Nội', '02438221112', 'nxtc@nxft.vn', N'Phạm Văn A', N'NXB chuyên về sách kinh tế và tài chính'),
    (5, N'Nhà xuất bản Đại học Quốc gia', N'Hà Nội', '02438241113', 'nxdhqg@dhqg.vn', N'Ngô Thị B', N'NXB đại học uy tín'),
    (6, N'Nhà xuất bản Mỹ thuật', N'TP.HCM', '02838221114', 'nxmt@nxmt.vn', N'Vũ Văn C', N'NXB chuyên về sách nghệ thuật và thiết kế'),
    (7, N'Nhà xuất bản Ẩm thực', N'TP.HCM', '02838221115', 'nxat@nxat.vn', N'Đặng Thị D', N'NXB chuyên về sách ẩm thực và nấu ăn'),
    (8, N'Nhà xuất bản Khoa học', N'Hà Nội', '02438221116', 'nxkh@nxkh.vn', N'Bùi Văn E', N'NXB chuyên về sách khoa học và công nghệ'),
    (9, N'Nhà xuất bản Văn hóa', N'TP.HCM', '02838221117', 'nxvh@nxvh.vn', N'Hoàng Thị F', N'NXB về văn hóa và lịch sử'),
    (10, N'Nhà xuất bản Ngoại ngữ', N'Hà Nội', '02438221118', 'nxnn@nxnn.vn', N'Ngô Văn G', N'NXB chuyên về sách ngoại ngữ'),
    (11, N'Nhà xuất bản Thể thao', N'TP.HCM', '02838221119', 'nxtt@nxtt.vn', N'Lê Thị H', N'NXB về sách thể thao và sức khỏe'),
    (12, N'Nhà xuất bản Pháp luật', N'Hà Nội', '02438221120', 'nxpl@nxpl.vn', N'Trần Văn I', N'NXB chuyên về sách pháp luật'),
    (13, N'Nhà xuất bản Lao động', N'Hà Nội', '02438221121', 'nxbld@nxbld.vn', N'Vũ Văn D', N'NXB sách kỹ năng và lao động'),
    (14, N'Nhà xuất bản Chính trị Quốc gia', N'Hà Nội', '02438221122', 'nxbct@nxbct.vn', N'Đặng Thị E', N'NXB sách chính trị và pháp luật'),
    (15, N'Nhà xuất bản Đại học Sư phạm', N'TP.HCM', '02838221123', 'nxbdhsp@dhsp.vn', N'Bùi Văn F', N'NXB sách giáo dục đại học'),
    (16, N'Nhà xuất bản Thông tin và Truyền thông', N'Hà Nội', '02438221124', 'nxbtt@nxbtt.vn', N'Hoàng Thị G', N'NXB sách công nghệ thông tin'),
    (17, N'Nhà xuất bản Xây dựng', N'Hà Nội', '02438221125', 'nxbxd@nxbxd.vn', N'Ngô Văn H', N'NXB sách xây dựng và kiến trúc'),
    (18, N'Nhà xuất bản Y học', N'TP.HCM', '02838221126', 'nxbyh@nxbyh.vn', N'Phạm Thị I', N'NXB sách y học và sức khỏe'),
    (19, N'Nhà xuất bản Nông nghiệp', N'Hà Nội', '02438221127', 'nxbnn@nxbnn.vn', N'Trần Văn J', N'NXB sách nông nghiệp và sinh học'),
    (20, N'Nhà xuất bản Giao thông Vận tải', N'Hà Nội', '02438221128', 'nxbgt@nxbgt.vn', N'Lê Thị K', N'NXB sách giao thông và vận tải');
SET IDENTITY_INSERT NhaCungCap OFF;
GO

PRINT N'Đã tạo database và thêm dữ liệu thành công!';
GO