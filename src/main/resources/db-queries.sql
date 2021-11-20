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

-- insert all new tags from a specified array that do not yet exist in the table (case-insensitive)
insert into tag (name)
select *
from (select lower(tag) from unnest(array ['ESSENTIALISM', 'GROW', 'SPIRITUAL', 'SCIENCE']) as tag) new_tags
except
select lower(name)
from tag;
