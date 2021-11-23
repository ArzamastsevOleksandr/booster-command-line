package booster.service;

import booster.command.arguments.TrainingSessionMode;
import booster.dao.VocabularyEntryDao;
import booster.dao.params.AddTagToVocabularyEntryDaoParams;
import booster.dao.params.AddVocabularyEntryDaoParams;
import booster.dao.params.UpdateVocabularyEntryDaoParams;
import booster.model.VocabularyEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class VocabularyEntryService {

    private static final int MIN_CORRECT_ANSWERS_COUNT = 0;

    private final VocabularyEntryDao vocabularyEntryDao;
    private final TransactionTemplate transactionTemplate;
    private final SessionTrackerService sessionTrackerService;

    public Optional<VocabularyEntry> findById(long id) {
        if (existsWithId(id)) {
            VocabularyEntry entry = vocabularyEntryDao.findById(id);

            Map<Long, Set<String>> synonymsById = vocabularyEntryDao.getSynonymsById(id);
            Map<Long, Set<String>> antonymsById = vocabularyEntryDao.getAntonymsById(id);
            Map<Long, Set<String>> tagsById = vocabularyEntryDao.getTagsByIdMap(id);
            Map<Long, Set<String>> contextsById = vocabularyEntryDao.getContextsByIdMap(id);

            return Optional.of(withCollections(synonymsById, antonymsById, tagsById, contextsById).apply(entry));
        }
        return Optional.empty();
    }

    public boolean existsWithId(long id) {
        return vocabularyEntryDao.countWithId(id) == 1;
    }

    public boolean existsWithWordIdAndLanguageId(long wordId, long languageId) {
        return vocabularyEntryDao.countWithWordIdAndLanguageId(wordId, languageId) == 1;
    }

    public boolean existAnyWithLanguageId(Long id) {
        return vocabularyEntryDao.countWithLanguageId(id) > 0;
    }

    public boolean existAnyWithSubstring(String substring) {
        return vocabularyEntryDao.countWithSubstring(substring) > 0;
    }

    public boolean existAny() {
        return vocabularyEntryDao.countAny() > 0;
    }

    public boolean existAnyForTrainingMode(TrainingSessionMode mode) {
        switch (mode) {
            case SYNONYMS:
                return vocabularyEntryDao.countWithSynonyms() > 0;
            case ANTONYMS:
                return vocabularyEntryDao.countWithAntonyms() > 0;
            case FULL:
                return vocabularyEntryDao.countWithAntonymsAndSynonyms() > 0;
            default:
                return false;
        }
    }

    public void delete(long id) {
        transactionTemplate.executeWithoutResult(result -> {
            vocabularyEntryDao.deleteSynonyms(id);
            vocabularyEntryDao.deleteAntonyms(id);
            vocabularyEntryDao.deleteContexts(id);
            vocabularyEntryDao.delete(id);
        });
    }

    public VocabularyEntry addTag(AddTagToVocabularyEntryDaoParams params) {
        vocabularyEntryDao.addTag(params.getTag(), params.getVocabularyEntryId());
        return findById(params.getVocabularyEntryId()).get();
    }

    public VocabularyEntry addWithDefaultValues(AddVocabularyEntryDaoParams params) {
        long id = vocabularyEntryDao.addWithDefaultValues(params);
        return addRemainingDetails(params, id);
    }

    public VocabularyEntry addWithAllValues(AddVocabularyEntryDaoParams params) {
        long id = vocabularyEntryDao.addWithAllValues(params);
        return addRemainingDetails(params, id);
    }

    private VocabularyEntry addRemainingDetails(AddVocabularyEntryDaoParams params, Long id) {
        Long newId = transactionTemplate.execute(status -> {
            vocabularyEntryDao.addSynonyms(new ArrayList<>(params.getSynonymIds()), id);
            vocabularyEntryDao.addAntonyms(new ArrayList<>(params.getAntonymIds()), id);
            vocabularyEntryDao.addContexts(new ArrayList<>(params.getContexts()), id);
            vocabularyEntryDao.addTags(new ArrayList<>(params.getTags()), id);
            return id;
        });
        sessionTrackerService.incVocabularyEntriesAddedCount();
        return findById(newId).get();
    }

    public List<VocabularyEntry> findAll() {
        List<VocabularyEntry> entries = vocabularyEntryDao.findAll();

        Map<Long, Set<String>> id2Synonyms = vocabularyEntryDao.getId2SynonymsMap();
        Map<Long, Set<String>> id2Antonyms = vocabularyEntryDao.getId2AntonymsMap();
        Map<Long, Set<String>> id2Tags = vocabularyEntryDao.getId2TagsMap();
        Map<Long, Set<String>> id2Contexts = vocabularyEntryDao.getId2ContextsMap();

        return entries.stream()
                .map(withCollections(id2Synonyms, id2Antonyms, id2Tags, id2Contexts))
                .collect(toList());
    }

    private Function<VocabularyEntry, VocabularyEntry> withCollections(Map<Long, Set<String>> id2Synonyms,
                                                                       Map<Long, Set<String>> id2Antonyms,
                                                                       Map<Long, Set<String>> id2Tags,
                                                                       Map<Long, Set<String>> id2Contexts) {
        return ve -> {
            var builder = ve.toBuilder();
            if (id2Synonyms.containsKey(ve.getId())) {
                builder = builder.synonyms(id2Synonyms.getOrDefault(ve.getId(), Set.of()));
            }
            if (id2Antonyms.containsKey(ve.getId())) {
                builder = builder.antonyms(id2Antonyms.getOrDefault(ve.getId(), Set.of()));
            }
            if (id2Tags.containsKey(ve.getId())) {
                builder = builder.tags(id2Tags.getOrDefault(ve.getId(), Set.of()));
            }
            if (id2Contexts.containsKey(ve.getId())) {
                builder = builder.contexts(id2Contexts.getOrDefault(ve.getId(), Set.of()));
            }
            return builder.build();
        };
    }

    public void updateCorrectAnswersCount(VocabularyEntry ve, boolean isCorrectAnswer) {
        int correctAnswersCountChange = isCorrectAnswer ? 1 : -1;
        int cacUpdated = ve.getCorrectAnswersCount() + correctAnswersCountChange;
        if (isValidCorrectAnswersCount(cacUpdated)) {
            vocabularyEntryDao.updateCorrectAnswersCount(ve.getId(), cacUpdated);
        }
    }

    private boolean isValidCorrectAnswersCount(int cacUpdated) {
        return MIN_CORRECT_ANSWERS_COUNT <= cacUpdated;
    }

    // todo: add tags?
    public List<VocabularyEntry> findAllWithSynonyms(int limit) {
        List<VocabularyEntry> entries = vocabularyEntryDao.findAllWithSynonyms(limit);
        // todo: do not load all synonyms, but only those that relate to entries above
        Map<Long, Set<String>> id2Synonyms = vocabularyEntryDao.getId2SynonymsMap();

        return entries.stream()
                .map(ve -> ve.toBuilder().synonyms(id2Synonyms.getOrDefault(ve.getId(), Set.of())).build())
                .collect(toList());
    }

    // todo: add tags?
    public List<VocabularyEntry> findAllWithAntonyms(int limit) {
        List<VocabularyEntry> entries = vocabularyEntryDao.findAllWithAntonyms(limit);
        Map<Long, Set<String>> id2Antonyms = vocabularyEntryDao.getId2AntonymsMap();
        // todo: do not load all antonyms, but only those that relate to entries above
        return entries.stream()
                .map(ve -> ve.toBuilder().antonyms(id2Antonyms.getOrDefault(ve.getId(), Set.of())).build())
                .collect(toList());
    }

    // todo: add tags?
    public List<VocabularyEntry> findAllWithAntonymsAndSynonyms(int limit) {
        List<VocabularyEntry> entries = vocabularyEntryDao.findAllWithAntonymsAndSynonyms(limit);
        Map<Long, Set<String>> id2Antonyms = vocabularyEntryDao.getId2AntonymsMap();
        Map<Long, Set<String>> id2Synonyms = vocabularyEntryDao.getId2SynonymsMap();
        // todo: do not load all synonyms/antonyms, but only those that relate to entries above
        return entries.stream()
                .map(ve -> ve.toBuilder()
                        .antonyms(id2Antonyms.getOrDefault(ve.getId(), Set.of()))
                        .synonyms(id2Synonyms.getOrDefault(ve.getId(), Set.of()))
                        .build()
                ).collect(toList());
    }

    public List<VocabularyEntry> findAllForLanguageId(long id) {
        List<VocabularyEntry> entries = vocabularyEntryDao.findAllForLanguageId(id);
        Map<Long, Set<String>> id2Synonyms = vocabularyEntryDao.getId2SynonymsMapForLanguageId(id);
        Map<Long, Set<String>> id2Antonyms = vocabularyEntryDao.getId2AntonymsMapForLanguageId(id);
        Map<Long, Set<String>> id2Tags = vocabularyEntryDao.getId2TagsMapForLanguageId(id);
        Map<Long, Set<String>> id2Contexts = vocabularyEntryDao.getId2ContextsMapForLanguageId(id);

        return entries.stream()
                .map(withCollections(id2Synonyms, id2Antonyms, id2Tags, id2Contexts))
                .collect(toList());
    }

    public void markDifficult(Long id, boolean isDifficult) {
        vocabularyEntryDao.markDifficult(id, isDifficult);
    }

    // todo: update tags
    // todo: optimize, do not update some parameters if not required
    public VocabularyEntry update(UpdateVocabularyEntryDaoParams params) {
        return transactionTemplate.execute(result -> {
            vocabularyEntryDao.updateVocabularyEntry(params);
            vocabularyEntryDao.deleteSynonymsById(params.getId());
            vocabularyEntryDao.deleteAntonymsById(params.getId());

            vocabularyEntryDao.addSynonyms(new ArrayList<>(params.getSynonymIds()), params.getId());
            vocabularyEntryDao.addAntonyms(new ArrayList<>(params.getAntonymIds()), params.getId());
            return vocabularyEntryDao.findById(params.getId());
        });
    }

    // todo: use range OR veIds for synonyms, antonyms, contexts, tags to improve performance
    public List<VocabularyEntry> findAllInRange(int startInclusive, int endInclusive) {
        List<VocabularyEntry> entries = vocabularyEntryDao.findAllInRange(startInclusive, endInclusive);

        Map<Long, Set<String>> id2Synonyms = vocabularyEntryDao.getId2SynonymsMap();
        Map<Long, Set<String>> id2Antonyms = vocabularyEntryDao.getId2AntonymsMap();
        Map<Long, Set<String>> id2Tags = vocabularyEntryDao.getId2TagsMap();
        Map<Long, Set<String>> id2Contexts = vocabularyEntryDao.getId2ContextsMap();

        return entries.stream()
                .map(withCollections(id2Synonyms, id2Antonyms, id2Tags, id2Contexts))
                .collect(toList());
    }

    // todo: use veIds for synonyms, antonyms, contexts, tags to improve performance
    public List<VocabularyEntry> findAllWithSubstring(String substring) {
        List<VocabularyEntry> entries = vocabularyEntryDao.findAllWithSubstring(substring);

        Map<Long, Set<String>> id2Synonyms = vocabularyEntryDao.getId2SynonymsMap();
        Map<Long, Set<String>> id2Antonyms = vocabularyEntryDao.getId2AntonymsMap();
        Map<Long, Set<String>> id2Tags = vocabularyEntryDao.getId2TagsMap();
        Map<Long, Set<String>> id2Contexts = vocabularyEntryDao.getId2ContextsMap();

        return entries.stream()
                .map(withCollections(id2Synonyms, id2Antonyms, id2Tags, id2Contexts))
                .collect(toList());
    }

    // todo: use veIds for synonyms, antonyms, contexts, tags to improve performance
    public List<VocabularyEntry> findAllInRangeWithSubstring(int startInclusive, int endInclusive, String substring) {
        List<VocabularyEntry> entries = vocabularyEntryDao.findAllInRangeWithSubstring(startInclusive, endInclusive, substring);

        Map<Long, Set<String>> id2Synonyms = vocabularyEntryDao.getId2SynonymsMap();
        Map<Long, Set<String>> id2Antonyms = vocabularyEntryDao.getId2AntonymsMap();
        Map<Long, Set<String>> id2Tags = vocabularyEntryDao.getId2TagsMap();
        Map<Long, Set<String>> id2Contexts = vocabularyEntryDao.getId2ContextsMap();

        return entries.stream()
                .map(withCollections(id2Synonyms, id2Antonyms, id2Tags, id2Contexts))
                .collect(toList());
    }

    public int countTotal() {
        return vocabularyEntryDao.countTotal();
    }

    public int countWithSubstring(String substring) {
        return vocabularyEntryDao.countWithSubstring(substring);
    }

}
