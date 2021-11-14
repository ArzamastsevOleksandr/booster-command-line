-- how many vocabulary entries have synonyms?
select count(distinct vocabulary_entry_id)
from vocabulary_entry__synonym__jt;

-- how many vocabulary entries have antonyms?
select count(distinct vocabulary_entry_id)
from vocabulary_entry__antonym__jt;

-- how many vocabulary entries have BOTH antonyms and synonyms?
select distinct vocabulary_entry_id
from vocabulary_entry__synonym__jt
intersect
select distinct vocabulary_entry_id
from vocabulary_entry__antonym__jt;
