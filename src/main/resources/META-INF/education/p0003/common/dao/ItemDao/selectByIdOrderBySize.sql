select
  /*%expand*/*
from
  item_tbl
where
  id in /* ids */(1,2)
order by
  size desc