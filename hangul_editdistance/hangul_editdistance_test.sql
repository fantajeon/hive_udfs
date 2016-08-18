add jar ./hangul_editdistance.jar;
create temporary function sch_HangulEditDistance as 'org.sch.udf.HangulEditDistance';
select sch_HangulEditDistance('ㅇㅂㅇㅊ', '오버워치');
