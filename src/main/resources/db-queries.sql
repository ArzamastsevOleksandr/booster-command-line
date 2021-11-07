-- how many vocabulary entries have synonyms?
select count(distinct ve_with_synonyms.id)
from (select *
      from vocabulary_entry ve
      where exists(select * from vocabulary_entry__synonym__jt where vocabulary_entry_id = ve.id)) ve_with_synonyms;

-- how many vocabulary entries have antonyms?
select count(distinct ve_with_antonyms.id)
from (select *
      from vocabulary_entry ve
      where exists(select * from vocabulary_entry__antonym__jt where vocabulary_entry_id = ve.id)) ve_with_antonyms;

-- how many vocabulary entries have BOTH antonyms and synonyms?
select count(distinct ve_with_antonyms_and_synonyms.id)
from (select *
      from vocabulary_entry ve
      where exists(select * from vocabulary_entry__antonym__jt where vocabulary_entry_id = ve.id)
        and exists(select * from vocabulary_entry__synonym__jt where vocabulary_entry_id = ve.id)
) ve_with_antonyms_and_synonyms;
