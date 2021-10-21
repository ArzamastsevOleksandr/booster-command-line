insert into language
    (id, name)
values (1, 'ENGLISH'),
       (2, 'GERMAN');

-- Create language being learned records for every language
insert into language_being_learned
    (language_id)
select l.id
from language l;

-- Create a default vocabulary for every language being learned
insert into vocabulary
    (name, language_being_learned_id)
select 'Default', lbl.id
from language_being_learned lbl;

insert into word
    (id, name)
values (1, 'fallacy'),
       (2, 'misconception'),
       (3, 'falsehood'),
       (4, 'heresy'),
       (5, 'truth'),
       (6, 'fairness'),
       (7, 'pundit'),
       (8, 'expert'),
       (9, 'intellectual'),
       (10, 'amateur');

insert into vocabulary_entry
    (id, word_id, vocabulary_id)
values (1, 1, 1),
       (2, 7, 1);

insert into vocabulary_entry__synonym__jt
    (vocabulary_entry_id, word_id)
values (1, 2),
       (1, 3),
       (1, 4),
       (2, 8),
       (2, 9);

insert into vocabulary_entry__antonym__jt
    (vocabulary_entry_id, word_id)
values (1, 5),
       (1, 6),
       (2, 10);
