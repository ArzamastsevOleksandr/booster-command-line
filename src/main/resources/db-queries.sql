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

-- find all vocabulary entries that have a substring either in word/synonym/antonym/contexts
with substr(s) as (
    values ('%ka%')
)
select ve_out.id as ve_id, ve_out.created_at, ve_out.last_seen_at, ve_out.correct_answers_count as cac, ve_out.definition as definition,
       w_out.name as w_name, w_out.id as w_id,
       l.name as l_name, l.id as l_id
from vocabulary_entry ve_out
join word w_out
on ve_out.word_id = w_out.id
join language l
on ve_out.language_id = l.id
where exists(select * from word where id = ve_out.word_id and name like (select s from substr))
   or exists(select * from vocabulary_entry__synonym__jt vesj join word w on vesj.word_id = w.id where vocabulary_entry_id = ve_out.id and w.name like (select s from substr))
   or exists(select * from vocabulary_entry__antonym__jt veaj join word w on veaj.word_id = w.id where vocabulary_entry_id = ve_out.id and w.name like (select s from substr))
   or exists(select * from vocabulary_entry__context__jt vecj where vocabulary_entry_id = ve_out.id and context like (select s from substr))
order by ve_out.last_seen_at
limit 2;

-- count how many vocabulary entries have a substring in the word/synonyms/antonyms/contexts
with substr(s) as (
    values ('%ka%')
)
select count(*)
from vocabulary_entry ve_out
join word w_out
on ve_out.word_id = w_out.id
where exists(select * from word where id = ve_out.word_id and name like (select s from substr))
   or exists(select * from vocabulary_entry__synonym__jt vesj join word w on vesj.word_id = w.id where vocabulary_entry_id = ve_out.id and w.name like (select s from substr))
   or exists(select * from vocabulary_entry__antonym__jt veaj join word w on veaj.word_id = w.id where vocabulary_entry_id = ve_out.id and w.name like (select s from substr))
   or exists(select * from vocabulary_entry__context__jt vecj where vocabulary_entry_id = ve_out.id and context like (select s from substr));