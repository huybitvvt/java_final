-- Database Configuration for SQL Server
-- Run this script in SQL Server Management Studio (SSMS)

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

-- Insert sample data
-- ============================================
-- NHÂN VIÊN (12 nhân viên)
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
    (12, N'Ngô Thị L', N'Nữ', '1995-10-08', '0923456780', 'ntl@bookstore.com', N'TP.HCM', N'Thu ngân', 7800000, '2022-02-01', N'Hoạt động');
SET IDENTITY_INSERT NhanVien OFF;



-- ============================================
-- KHÁCH HÀNG (35 khách)
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
    (35, N'Trần Thị AL', N'Nữ', '1999-08-17', '0989012349', 'ntal@email.com', N'Cần Thơ', 20);
SET IDENTITY_INSERT KhachHang OFF;

-- ============================================
-- NHÀ CUNG CẤP (12 nhà cung cấp)
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
    (12, N'Nhà xuất bản Pháp luật', N'Hà Nội', '02438221120', 'nxpl@nxpl.vn', N'Trần Văn I', N'NXB chuyên về sách pháp luật');
SET IDENTITY_INSERT NhaCungCap OFF;

-- ============================================
-- SÁCH (55 cuốn)
-- ============================================
SET IDENTITY_INSERT Sach ON;
INSERT INTO Sach (MaSach, TenSach, TacGia, NhaXuatBan, TheLoai, NamXuatBan, GiaBia, SoLuongTon, MoTa)
VALUES
    (1, N'Lập trình Java cơ bản', N'Nguyễn Văn A', N'Nhà xuất bản Khoa học', N'Công nghệ', 2023, 89000, 120, N'Học Java từ cơ bản đến nâng cao'),
    (2, N'Python cho người mới bắt đầu', N'Trần Thị B', N'Nhà xuất bản Khoa học', N'Công nghệ', 2023, 79000, 150, N'Python cơ bản cho beginners'),
    (3, N'Cấu trúc dữ liệu giải thuật', N'Lê Văn C', N'Nhà xuất bản Giáo dục', N'Giáo khoa', 2022, 95000, 80, N'Giáo trình CNTT'),
    (4, N'Marketing đỉnh cao', N'Nguyễn Thị D', N'Nhà xuất bản Tài chính', N'Kinh tế', 2024, 120000, 95, N'Chiến lược marketing hiệu quả'),
    (5, N'Tiếng Anh giao tiếp', N'Hoàng Văn E', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2023, 85000, 200, N'Tiếng Anh thực hành'),
    (6, N'Kế toán doanh nghiệp', N'Vũ Thị F', N'Nhà xuất bản Tài chính', N'Kinh tế', 2023, 150000, 65, N'Hướng dẫn kế toán chi tiết'),
    (7, N'Thiết kế đồ họa', N'Đặng Văn G', N'Nhà xuất bản Mỹ thuật', N'Mỹ thuật', 2024, 180000, 55, N'Học thiết kế đồ họa chuyên nghiệp'),
    (8, N'Nấu ăn ngon', N'Bùi Thị H', N'Nhà xuất bản Ẩm thực', N'Ẩm thực', 2023, 95000, 140, N'Công thức nấu ăn Việt Nam'),
    (9, N'Lập trình Web với HTML, CSS, JS', N'Bùi Văn I', N'Nhà xuất bản Khoa học', N'Công nghệ', 2023, 95000, 90, N'Học lập trình web từ cơ bản'),
    (10, N'Tiếng Nhật sơ cấp', N'Nguyễn Thị J', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2022, 85000, 110, N'Tiếng Nhật cho người mới bắt đầu'),
    (11, N'Vật lý đại cương', N'Trần Văn K', N'Nhà xuất bản Giáo dục', N'Giáo khoa', 2021, 135000, 70, N'Sách giáo trình vật lý đại học'),
    (12, N'Hóa học hữu cơ', N'Lê Thị L', N'Nhà xuất bản Giáo dục', N'Giáo khoa', 2022, 145000, 60, N'Hóa học hữu cơ đại học'),
    (13, N'Triết học Mac-Lenin', N'Phạm Văn M', N'Nhà xuất bản Giáo dục', N'Giáo khoa', 2020, 55000, 100, N'Giáo trình triết học'),
    (14, N'Marketing căn bản', N'Hoàng Thị N', N'Nhà xuất bản Tài chính', N'Kinh tế', 2023, 120000, 85, N'Kiến thức marketing cơ bản'),
    (15, N'Thiết kế nội thất', N'Vũ Văn O', N'Nhà xuất bản Mỹ thuật', N'Mỹ thuật', 2022, 165000, 45, N'Học thiết kế nội thất hiện đại'),
    (16, N'Ẩm thực Việt Nam', N'Đặng Thị P', N'Nhà xuất bản Ẩm thực', N'Ẩm thực', 2023, 95000, 130, N'Các món ăn truyền thống Việt Nam'),
    (17, N'Cấu trúc dữ liệu và giải thuật', N'Bùi Văn Q', N'Nhà xuất bản Khoa học', N'Công nghệ', 2022, 155000, 75, N'Học CSDL và thuật toán'),
    (18, N'Tiếng Trung giao tiếp', N'Ngô Thị R', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2023, 78000, 140, N'Tiếng Trung cho người đi làm'),
    (19, N'Sinh học đại cương', N'Nguyễn Văn S', N'Nhà xuất bản Giáo dục', N'Giáo khoa', 2021, 110000, 65, N'Sinh học đại cương'),
    (20, N'Kế toán tài chính', N'Trần Thị T', N'Nhà xuất bản Tài chính', N'Kinh tế', 2023, 175000, 50, N'Kế toán tài chính doanh nghiệp'),
    (21, N'Lịch sử thế giới', N'Lê Văn U', N'Nhà xuất bản Văn hóa', N'Lịch sử', 2021, 105000, 55, N'Tổng quan lịch sử thế giới'),
    (22, N'Nhiếp ảnh cơ bản', N'Phạm Thị V', N'Nhà xuất bản Mỹ thuật', N'Mỹ thuật', 2022, 135000, 40, N'Học nhiếp ảnh từ cơ bản'),
    (23, N'Phong cách sống', N'Hoàng Văn W', N'Nhà xuất bản Văn hóa', N'Đời sống', 2023, 68000, 95, N'Phong cách sống hiện đại'),
    (24, N'Lập trình C++', N'Vũ Văn X', N'Nhà xuất bản Khoa học', N'Công nghệ', 2022, 98000, 85, N'Lập trình C++ từ cơ bản'),
    (25, N'Toán rời rạc', N'Đặng Văn Y', N'Nhà xuất bản Giáo dục', N'Giáo khoa', 2021, 125000, 70, N'Toán rời rạc cho CNTT'),
    (26, N'Quản trị doanh nghiệp', N'Bùi Thị Z', N'Nhà xuất bản Tài chính', N'Kinh tế', 2023, 160000, 60, N'Quản trị doanh nghiệp hiện đại'),
    (27, N'Yoga cho sức khỏe', N'Ngô Văn AA', N'Nhà xuất bản Thể thao', N'Sức khỏe', 2023, 75000, 110, N'Bài tập yoga tại nhà'),
    (28, N'Tiếng Hàn giao tiếp', N'Nguyễn Thị AB', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2023, 82000, 125, N'Tiếng Hàn cho người mới'),
    (29, N'Pháp luật đại cương', N'Trần Văn AC', N'Nhà xuất bản Pháp luật', N'Pháp luật', 2022, 65000, 80, N'Giáo trình pháp luật'),
    (30, N'Thiết kế thời trang', N'Lê Thị AD', N'Nhà xuất bản Mỹ thuật', N'Mỹ thuật', 2023, 145000, 35, N'Học thiết kế thời trang'),
    (31, N'Kinh tế vĩ mô', N'Phạm Văn AE', N'Nhà xuất bản Tài chính', N'Kinh tế', 2022, 140000, 65, N'Kinh tế vĩ mô căn bản'),
    (32, N'Công nghệ thông tin', N'Hoàng Văn AF', N'Nhà xuất bản Khoa học', N'Công nghệ', 2023, 168000, 55, N'Tổng quan CNTT'),
    (33, N'Giáo dục công dân', N'Vũ Thị AG', N'Nhà xuất bản Giáo dục', N'Giáo khoa', 2021, 45000, 120, N'Giáo dục công dân'),
    (34, N'Âm nhạc căn bản', N'Đặng Văn AH', N'Nhà xuất bản Văn hóa', N'Nghệ thuật', 2022, 85000, 70, N'Học nhạc cơ bản'),
    (35, N'Dinh dưỡng và sức khỏe', N'Bùi Thị AI', N'Nhà xuất bản Thể thao', N'Sức khỏe', 2023, 92000, 90, N'Chế độ dinh dưỡng lành mạnh'),
    (36, N'Tiếng Đức sơ cấp', N'Ngô Văn AJ', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2022, 88000, 75, N'Tiếng Đức cho người mới'),
    (37, N'Lập trình Android', N'Nguyễn Thị AK', N'Nhà xuất bản Khoa học', N'Công nghệ', 2023, 138000, 80, N'Lập trình ứng dụng Android'),
    (38, N'Xác suất thống kê', N'Trần Văn AL', N'Nhà xuất bản Giáo dục', N'Giáo khoa', 2021, 115000, 60, N'Xác suất và thống kê'),
    (39, N'Tài chính doanh nghiệp', N'Lê Thị AM', N'Nhà xuất bản Tài chính', N'Kinh tế', 2023, 185000, 45, N'Tài chính doanh nghiệp'),
    (40, N'Địa lý Việt Nam', N'Phạm Văn AN', N'Nhà xuất bản Giáo dục', N'Giáo khoa', 2020, 75000, 85, N'Địa lý Việt Nam'),
    (41, N'Múa đương đại', N'Hoàng Thị AO', N'Nhà xuất bản Văn hóa', N'Nghệ thuật', 2022, 98000, 40, N'Học múa đương đại'),
    (42, N'Phát triển cá nhân', N'Vũ Văn AP', N'Nhà xuất bản Văn hóa', N'Đời sống', 2023, 72000, 105, N'Phát triển bản thân'),
    (43, N'Lập trình iOS', N'Đặng Thị AQ', N'Nhà xuất bản Khoa học', N'Công nghệ', 2023, 142000, 70, N'Lập trình ứng dụng iOS'),
    (44, N'Tiếng Pháp giao tiếp', N'Bùi Văn AR', N'Nhà xuất bản Ngoại ngữ', N'Ngoại ngữ', 2023, 79000, 115, N'Tiếng Pháp thực hành'),
    (45, N'Kinh tế vi mô', N'Ngô Thị AS', N'Nhà xuất bản Tài chính', N'Kinh tế', 2022, 135000, 70, N'Kinh tế vi mô căn bản'),
    (46, N'Tâm lý học đại cương', N'Nguyễn Văn AT', N'Nhà xuất bản Văn hóa', N'Khoa học xã hội', 2021, 95000, 65, N'Tâm lý học căn bản'),
    (47, N'Marketing online', N'Trần Thị AU', N'Nhà xuất bản Tài chính', N'Kinh tế', 2023, 128000, 90, N'Marketing trực tuyến'),
    (48, N'Vẽ kỹ thuật', N'Lê Văn AV', N'Nhà xuất bản Mỹ thuật', N'Mỹ thuật', 2021, 115000, 50, N'Vẽ kỹ thuật cơ bản'),
    (49, N'Chăm sóc da và làm đẹp', N'Phạm Thị AW', N'Nhà xuất bản Thể thao', N'Sức khỏe', 2023, 88000, 100, N'Chăm sóc sắc đẹp'),
    (50, N'Lập trình NodeJS', N'Hoàng Văn AX', N'Nhà xuất bản Khoa học', N'Công nghệ', 2023, 135000, 75, N'Lập trình backend với NodeJS'),
    (51, N'Giao tiếp ứng xử', N'Vũ Thị AY', N'Nhà xuất bản Văn hóa', N'Khoa học xã hội', 2022, 62000, 110, N'Nghệ thuật giao tiếp'),
    (52, N'Thể dục thể thao', N'Đặng Văn AZ', N'Nhà xuất bản Thể thao', N'Thể thao', 2021, 55000, 95, N'Giáo dục thể chất'),
    (53, N'Khoa học máy tính', N'Bùi Văn BA', N'Nhà xuất bản Khoa học', N'Công nghệ', 2022, 198000, 50, N'Cơ bản khoa học máy tính'),
    (54, N'Văn học Việt Nam', N'Ngô Thị BB', N'Nhà xuất bản Văn hóa', N'Văn học', 2020, 78000, 70, N'Tác phẩm văn học Việt Nam'),
    (55, N'Quản lý nhân sự', N'Nguyễn Văn BC', N'Nhà xuất bản Tài chính', N'Kinh tế', 2023, 148000, 55, N'Quản trị nhân sự');
SET IDENTITY_INSERT Sach OFF;

-- ============================================
-- HÓA ĐƠN (55 hóa đơn)
-- ============================================
INSERT INTO HoaDon (MaHoaDonString, MaKhachHang, MaNhanVien, NgayLap, TongTien, GiamGia, ThanhToan, PhuongThucThanhToan, GhiChu, TrangThai)
VALUES
    ('HD00001', 1, 2, '2024-01-05', 178000, 0, 178000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00002', 2, 3, '2024-01-06', 237000, 0, 237000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00003', 3, 2, '2024-01-07', 145000, 0, 145000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00004', 4, 5, '2024-01-08', 356000, 10000, 346000, N'Tiền mặt', N'Giảm giá 10k', N'Đã thanh toán'),
    ('HD00005', 5, 3, '2024-01-09', 189000, 0, 189000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00006', 6, 2, '2024-01-10', 420000, 20000, 400000, N'Tiền mặt', N'Giảm giá 20k', N'Đã thanh toán'),
    ('HD00007', 7, 6, '2024-01-11', 95000, 0, 95000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00008', 8, 5, '2024-01-12', 268000, 0, 268000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00009', 9, 3, '2024-01-13', 315000, 15000, 300000, N'Tiền mặt', N'Giảm giá 15k', N'Đã thanh toán'),
    ('HD00010', 10, 2, '2024-01-14', 198000, 0, 198000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00011', 11, 7, '2024-01-15', 456000, 0, 456000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00012', 12, 5, '2024-01-16', 167000, 0, 167000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00013', 13, 3, '2024-01-17', 289000, 5000, 284000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00014', 14, 2, '2024-01-18', 378000, 0, 378000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00015', 15, 8, '2024-01-19', 215000, 0, 215000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00016', 16, 5, '2024-01-20', 534000, 30000, 504000, N'Tiền mặt', N'VIP giảm 30k', N'Đã thanh toán'),
    ('HD00017', 17, 3, '2024-01-21', 156000, 0, 156000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00018', 18, 2, '2024-01-22', 423000, 0, 423000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00019', 19, 7, '2024-01-23', 198000, 0, 198000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00020', 20, 5, '2024-01-24', 367000, 10000, 357000, N'Tiền mặt', N'Giảm giá 10k', N'Đã thanh toán'),
    ('HD00021', 21, 3, '2024-01-25', 245000, 0, 245000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00022', 22, 2, '2024-01-26', 178000, 0, 178000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00023', 23, 8, '2024-01-27', 512000, 0, 512000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00024', 24, 5, '2024-01-28', 289000, 0, 289000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00025', 25, 3, '2024-01-29', 445000, 20000, 425000, N'Tiền mặt', N'Giảm giá 20k', N'Đã thanh toán'),
    ('HD00026', 26, 2, '2024-01-30', 167000, 0, 167000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00027', 27, 7, '2024-01-31', 356000, 0, 356000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00028', 28, 5, '2024-02-01', 234000, 0, 234000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00029', 29, 3, '2024-02-02', 478000, 25000, 453000, N'Tiền mặt', N'Giảm giá 25k', N'Đã thanh toán'),
    ('HD00030', 30, 2, '2024-02-03', 189000, 0, 189000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00031', 31, 8, '2024-02-04', 567000, 0, 567000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00032', 32, 5, '2024-02-05', 312000, 0, 312000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00033', 33, 3, '2024-02-06', 198000, 0, 198000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00034', 34, 2, '2024-02-07', 423000, 15000, 408000, N'Tiền mặt', N'Giảm giá 15k', N'Đã thanh toán'),
    ('HD00035', 35, 7, '2024-02-08', 256000, 0, 256000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00036', 1, 5, '2024-02-09', 389000, 0, 389000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00037', 2, 3, '2024-02-10', 178000, 0, 178000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00038', 3, 2, '2024-02-11', 445000, 20000, 425000, N'Tiền mặt', N'Giảm giá 20k', N'Đã thanh toán'),
    ('HD00039', 4, 8, '2024-02-12', 267000, 0, 267000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00040', 5, 5, '2024-02-13', 534000, 0, 534000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00041', 6, 3, '2024-02-14', 198000, 0, 198000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00042', 7, 2, '2024-02-15', 378000, 10000, 368000, N'Tiền mặt', N'Giảm giá 10k', N'Đã thanh toán'),
    ('HD00043', 8, 7, '2024-02-16', 456000, 0, 456000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00044', 9, 5, '2024-02-17', 289000, 0, 289000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00045', 10, 3, '2024-02-18', 167000, 0, 167000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00046', 11, 2, '2024-02-19', 512000, 30000, 482000, N'Tiền mặt', N'VIP giảm 30k', N'Đã thanh toán'),
    ('HD00047', 12, 8, '2024-02-20', 234000, 0, 234000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00048', 13, 5, '2024-02-21', 398000, 0, 398000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00049', 14, 3, '2024-02-22', 178000, 0, 178000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00050', 15, 2, '2024-02-23', 445000, 0, 445000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00051', 16, 7, '2024-02-24', 267000, 5000, 262000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00052', 17, 5, '2024-02-25', 356000, 0, 356000, N'Chuyển khoản', N'', N'Đã thanh toán'),
    ('HD00053', 18, 3, '2024-02-26', 489000, 20000, 469000, N'Tiền mặt', N'Giảm giá 20k', N'Đã thanh toán'),
    ('HD00054', 19, 2, '2024-02-27', 198000, 0, 198000, N'Tiền mặt', N'', N'Đã thanh toán'),
    ('HD00055', 20, 8, '2024-02-28', 623000, 35000, 588000, N'Tiền mặt', N'VIP giảm 35k', N'Đã thanh toán');

-- ============================================
-- CHI TIẾT HÓA ĐƠN
-- ============================================
INSERT INTO ChiTietHoaDon (MaHoaDon, MaSach, SoLuong, DonGia, ThanhTien, GiamGia)
VALUES
    (1, 1, 2, 89000, 178000, 0),
    (2, 3, 2, 65000, 130000, 0),
    (2, 9, 1, 95000, 95000, 0),
    (3, 2, 1, 79000, 79000, 0),
    (3, 10, 1, 85000, 85000, 0),
    (4, 4, 2, 120000, 240000, 0),
    (4, 14, 1, 120000, 120000, 0),
    (5, 5, 2, 95000, 190000, 0),
    (6, 6, 2, 150000, 300000, 0),
    (6, 15, 1, 180000, 180000, 0),
    (7, 7, 1, 180000, 180000, 0),
    (8, 8, 2, 75000, 150000, 0),
    (8, 23, 2, 68000, 136000, 0),
    (9, 17, 2, 155000, 310000, 0),
    (10, 3, 3, 65000, 195000, 0),
    (11, 4, 3, 120000, 360000, 0),
    (11, 20, 1, 175000, 175000, 0),
    (12, 1, 1, 89000, 89000, 0),
    (12, 9, 1, 95000, 95000, 0),
    (13, 2, 2, 79000, 158000, 0),
    (13, 24, 2, 98000, 196000, 0),
    (14, 11, 2, 135000, 270000, 0),
    (14, 19, 1, 110000, 110000, 0),
    (15, 5, 2, 95000, 190000, 0),
    (16, 6, 3, 150000, 450000, 0),
    (17, 12, 1, 145000, 145000, 0),
    (18, 13, 2, 55000, 110000, 0),
    (18, 26, 2, 160000, 320000, 0),
    (19, 16, 2, 75000, 150000, 0),
    (20, 21, 2, 105000, 210000, 0),
    (20, 29, 2, 65000, 130000, 0),
    (21, 18, 3, 78000, 234000, 0),
    (22, 22, 1, 135000, 135000, 0),
    (22, 30, 1, 145000, 145000, 0),
    (23, 25, 3, 125000, 375000, 0),
    (23, 31, 1, 140000, 140000, 0),
    (24, 27, 2, 75000, 150000, 0),
    (24, 28, 2, 82000, 164000, 0),
    (25, 32, 2, 168000, 336000, 0),
    (25, 33, 2, 45000, 90000, 0),
    (26, 34, 2, 85000, 170000, 0),
    (27, 35, 2, 92000, 184000, 0),
    (27, 37, 1, 138000, 138000, 0),
    (28, 36, 2, 88000, 176000, 0),
    (29, 38, 2, 115000, 230000, 0),
    (29, 39, 1, 185000, 185000, 0),
    (30, 40, 2, 75000, 150000, 0),
    (31, 41, 2, 98000, 196000, 0),
    (31, 42, 3, 72000, 216000, 0),
    (31, 51, 3, 62000, 186000, 0),
    (32, 43, 2, 142000, 284000, 0),
    (32, 44, 1, 79000, 79000, 0),
    (33, 45, 1, 135000, 135000, 0),
    (33, 46, 1, 95000, 95000, 0),
    (34, 47, 2, 128000, 256000, 0),
    (34, 48, 1, 115000, 115000, 0),
    (35, 49, 2, 88000, 176000, 0),
    (35, 50, 1, 135000, 135000, 0),
    (36, 1, 2, 89000, 178000, 0),
    (36, 52, 3, 55000, 165000, 0),
    (37, 9, 1, 95000, 95000, 0),
    (37, 24, 1, 98000, 98000, 0),
    (38, 53, 2, 198000, 396000, 0),
    (38, 54, 1, 78000, 78000, 0),
    (39, 3, 2, 65000, 130000, 0),
    (39, 18, 1, 78000, 78000, 0),
    (40, 55, 3, 148000, 444000, 0),
    (41, 10, 2, 85000, 170000, 0),
    (42, 14, 2, 120000, 240000, 0),
    (42, 20, 1, 175000, 175000, 0),
    (43, 21, 2, 105000, 210000, 0),
    (43, 27, 2, 75000, 150000, 0),
    (44, 28, 2, 82000, 164000, 0),
    (44, 36, 1, 88000, 88000, 0),
    (45, 17, 1, 155000, 155000, 0),
    (46, 6, 2, 150000, 300000, 0),
    (46, 25, 1, 125000, 125000, 0),
    (47, 29, 2, 65000, 130000, 0),
    (47, 33, 2, 45000, 90000, 0),
    (48, 37, 2, 138000, 276000, 0),
    (48, 38, 1, 115000, 115000, 0),
    (49, 40, 2, 75000, 150000, 0),
    (50, 39, 2, 185000, 370000, 0),
    (50, 47, 1, 128000, 128000, 0),
    (51, 41, 2, 98000, 196000, 0),
    (51, 42, 1, 72000, 72000, 0),
    (52, 43, 1, 142000, 142000, 0),
    (52, 50, 1, 135000, 135000, 0),
    (52, 51, 2, 62000, 124000, 0),
    (53, 44, 2, 79000, 158000, 0),
    (53, 45, 2, 135000, 270000, 0),
    (53, 46, 1, 95000, 95000, 0),
    (54, 48, 1, 115000, 115000, 0),
    (54, 49, 1, 88000, 88000, 0),
    (55, 53, 2, 198000, 396000, 0),
    (55, 55, 1, 148000, 148000, 0),
    (55, 54, 1, 78000, 78000, 0);

-- ============================================
-- ĐƠN NHẬP HÀNG (25 đơn)
-- ============================================
INSERT INTO DonNhapHang (MaDonNhapString, MaNhaCungCap, MaNhanVien, NgayNhap, TongTien, GhiChu, TrangThai)
VALUES
    ('PN00001', 1, 4, '2024-01-03', 8900000, N'Nhập sách công nghệ', N'Đã nhập'),
    ('PN00002', 2, 4, '2024-01-05', 7110000, N'Nhập sách giáo khoa', N'Đã nhập'),
    ('PN00003', 3, 4, '2024-01-08', 4750000, N'Nhập sách thiếu nhi', N'Đã nhập'),
    ('PN00004', 4, 4, '2024-01-10', 12000000, N'Nhập sách kinh tế', N'Đã nhập'),
    ('PN00005', 5, 4, '2024-01-12', 9600000, N'Nhập sách đại học', N'Đã nhập'),
    ('PN00006', 6, 4, '2024-01-15', 6300000, N'Nhập sách mỹ thuật', N'Đã nhập'),
    ('PN00007', 7, 4, '2024-01-18', 5250000, N'Nhập sách ẩm thực', N'Đã nhập'),
    ('PN00008', 8, 4, '2024-01-20', 10800000, N'Nhập sách khoa học', N'Đã nhập'),
    ('PN00009', 9, 4, '2024-01-22', 7600000, N'Nhập sách văn hóa', N'Đã nhập'),
    ('PN00010', 10, 4, '2024-01-25', 5850000, N'Nhập sách ngoại ngữ', N'Đã nhập'),
    ('PN00011', 1, 4, '2024-02-01', 9300000, N'Nhập sách tháng 2', N'Đã nhập'),
    ('PN00012', 2, 4, '2024-02-05', 8400000, N'Nhập sách giáo khoa lần 2', N'Đã nhập'),
    ('PN00013', 3, 4, '2024-02-08', 5200000, N'Nhập sách thiếu nhi lần 2', N'Đã nhập'),
    ('PN00014', 4, 4, '2024-02-10', 13500000, N'Nhập sách kinh tế lần 2', N'Đã nhập'),
    ('PN00015', 5, 4, '2024-02-12', 10200000, N'Nhập sách đại học lần 2', N'Đã nhập'),
    ('PN00016', 6, 4, '2024-02-15', 6800000, N'Nhập sách mỹ thuật lần 2', N'Đã nhập'),
    ('PN00017', 7, 4, '2024-02-18', 5700000, N'Nhập sách ẩm thực lần 2', N'Đã nhập'),
    ('PN00018', 8, 4, '2024-02-20', 11500000, N'Nhập sách khoa học lần 2', N'Đã nhập'),
    ('PN00019', 9, 4, '2024-02-22', 8100000, N'Nhập sách văn hóa lần 2', N'Đã nhập'),
    ('PN00020', 10, 4, '2024-02-25', 6200000, N'Nhập sách ngoại ngữ lần 2', N'Đã nhập'),
    ('PN00021', 11, 4, '2024-02-28', 4500000, N'Nhập sách thể thao', N'Đã nhập'),
    ('PN00022', 12, 4, '2024-03-01', 3800000, N'Nhập sách pháp luật', N'Đã nhập'),
    ('PN00023', 1, 4, '2024-03-05', 9750000, N'Nhập sách tháng 3', N'Đã nhập'),
    ('PN00024', 2, 4, '2024-03-08', 9000000, N'Nhập sách giáo khoa lần 3', N'Đã nhập'),
    ('PN00025', 4, 4, '2024-03-10', 14200000, N'Nhập sách kinh tế lần 3', N'Đã nhập');

-- ============================================
-- CHI TIẾT ĐƠN NHẬP
-- ============================================
INSERT INTO ChiTietDonNhap (MaDonNhap, MaSach, SoLuong, DonGia, ThanhTien)
VALUES
    (1, 1, 100, 60000, 6000000),
    (1, 2, 50, 52000, 2600000),
    (1, 9, 30, 65000, 1950000),
    (2, 4, 60, 85000, 5100000),
    (2, 11, 15, 90000, 1350000),
    (3, 5, 50, 65000, 3250000),
    (3, 54, 20, 50000, 1000000),
    (4, 6, 80, 100000, 8000000),
    (4, 20, 25, 115000, 2875000),
    (4, 14, 15, 80000, 1200000),
    (5, 19, 70, 75000, 5250000),
    (5, 38, 30, 80000, 2400000),
    (5, 25, 20, 85000, 1700000),
    (6, 7, 35, 120000, 4200000),
    (6, 15, 15, 125000, 1875000),
    (7, 8, 70, 50000, 3500000),
    (7, 16, 25, 55000, 1375000),
    (8, 24, 60, 95000, 5700000),
    (8, 32, 30, 120000, 3600000),
    (8, 43, 15, 95000, 1425000),
    (9, 21, 50, 70000, 3500000),
    (9, 46, 40, 65000, 2600000),
    (9, 54, 20, 50000, 1000000),
    (10, 3, 60, 45000, 2700000),
    (10, 10, 25, 58000, 1450000),
    (10, 18, 25, 52000, 1300000),
    (11, 17, 60, 105000, 6300000),
    (11, 37, 25, 92000, 2300000),
    (12, 4, 50, 85000, 4250000),
    (12, 11, 20, 90000, 1800000),
    (12, 38, 25, 80000, 2000000),
    (13, 5, 40, 65000, 2600000),
    (13, 54, 30, 50000, 1500000),
    (13, 30, 15, 95000, 1425000),
    (14, 6, 70, 100000, 7000000),
    (14, 26, 40, 110000, 4400000),
    (14, 39, 25, 120000, 3000000),
    (15, 19, 60, 75000, 4500000),
    (15, 25, 30, 85000, 2550000),
    (15, 40, 40, 50000, 2000000),
    (16, 7, 30, 120000, 3600000),
    (16, 22, 25, 90000, 2250000),
    (17, 8, 60, 50000, 3000000),
    (17, 16, 30, 55000, 1650000),
    (18, 24, 55, 95000, 5225000),
    (18, 32, 35, 120000, 4200000),
    (18, 50, 25, 90000, 2250000),
    (19, 21, 45, 70000, 3150000),
    (19, 46, 35, 65000, 2275000),
    (19, 51, 45, 42000, 1890000),
    (20, 3, 55, 45000, 2475000),
    (20, 10, 30, 58000, 1740000),
    (20, 28, 30, 55000, 1650000),
    (21, 27, 40, 50000, 2000000),
    (21, 35, 30, 60000, 1800000),
    (22, 29, 50, 45000, 2250000),
    (22, 33, 35, 30000, 1050000),
    (23, 1, 80, 60000, 4800000),
    (23, 2, 60, 52000, 3120000),
    (24, 4, 55, 85000, 4675000),
    (24, 11, 25, 90000, 2250000),
    (25, 6, 75, 100000, 7500000),
    (25, 20, 35, 115000, 4025000),
    (25, 47, 30, 85000, 2550000);

-- ============================================
-- TÀI KHOẢN (Tài khoản đăng nhập) - Password là MD5
-- ============================================
SET IDENTITY_INSERT TaiKhoan ON;
INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, VaiTro, MaNhanVien, TrangThai)
VALUES
    (1, N'admin', N'e10adc3949ba59abbe56e057f20f883e', N'Quản lý', 1, N'Hoạt động'),
    (2, N'nv001', N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên', 2, N'Hoạt động'),
    (3, N'nv002', N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên', 3, N'Hoạt động'),
    (4, N'nv003', N'e10adc3949ba59abbe56e057f20f883e', N'Nhân viên', 4, N'Hoạt động');
SET IDENTITY_INSERT TaiKhoan OFF;

-- Create indexes


IF NOT EXISTS (SELECT name FROM sys.indexes WHERE name = 'idx_sach_ten')
CREATE INDEX idx_sach_ten ON Sach(TenSach);

IF NOT EXISTS (SELECT name FROM sys.indexes WHERE name = 'idx_sach_theloai')
CREATE INDEX idx_sach_theloai ON Sach(TheLoai);

IF NOT EXISTS (SELECT name FROM sys.indexes WHERE name = 'idx_khachhang_ten')
CREATE INDEX idx_khachhang_ten ON KhachHang(HoTen);

IF NOT EXISTS (SELECT name FROM sys.indexes WHERE name = 'idx_hoadon_ngay')
CREATE INDEX idx_hoadon_ngay ON HoaDon(NgayLap);

IF NOT EXISTS (SELECT name FROM sys.indexes WHERE name = 'idx_hoadon_makh')
CREATE INDEX idx_hoadon_makh ON HoaDon(MaKhachHang);