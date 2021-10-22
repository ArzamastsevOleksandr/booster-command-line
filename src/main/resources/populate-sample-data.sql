insert into language
    (name)
values ('ENGLISH'),
       ('GERMAN');

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
    (name)
values ('fallacy'),
       ('misconception'),
       ('falsehood'),
       ('heresy'),
       ('truth'),
       ('fairness'),
       ('pundit'),
       ('expert'),
       ('intellectual'),
       ('amateur');

insert into vocabulary_entry
    (word_id, vocabulary_id, definition)
values (1, 1, 'fallacy definition'),
       (7, 1, null);

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
