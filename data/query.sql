-- Bắt đầu transaction để an toàn
BEGIN;

-- Xem trước dữ liệu sẽ xóa
SELECT * FROM assets
WHERE symbol IN (
    'XAU', 'PSNTF', 'TSPPF', 'UTRK', 'PTEST', 'MTLZF', 
    'ORYYF', 'INMNF', 'BJURF', 'SVUFF', 'EOMFF', 'TMEEF', 
    'XNYEF', 'NJMLF', 'CHHQF', 'MWOES', 'WFICF', 'CTBK', 
    'IXEH', 'IDXAF', 'MWLDS', 'MWRTS', 'DPNEY'
);

-- Thực hiện xóa
DELETE FROM assets
WHERE symbol IN (
    'XAU', 'PSNTF', 'TSPPF', 'UTRK', 'PTEST', 'MTLZF', 
    'ORYYF', 'INMNF', 'BJURF', 'SVUFF', 'EOMFF', 'TMEEF', 
    'XNYEF', 'NJMLF', 'CHHQF', 'MWOES', 'WFICF', 'CTBK', 
    'IXEH', 'IDXAF', 'MWLDS', 'MWRTS', 'DPNEY'
);

-- Kiểm tra lại
SELECT * FROM assets
WHERE symbol IN (
    'XAU', 'PSNTF', 'TSPPF', 'UTRK', 'PTEST', 'MTLZF', 
    'ORYYF', 'INMNF', 'BJURF', 'SVUFF', 'EOMFF', 'TMEEF', 
    'XNYEF', 'NJMLF', 'CHHQF', 'MWOES', 'WFICF', 'CTBK', 
    'IXEH', 'IDXAF', 'MWLDS', 'MWRTS', 'DPNEY'
);

-- Nếu mọi thứ ổn, commit
COMMIT;
