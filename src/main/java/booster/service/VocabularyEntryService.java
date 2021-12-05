package booster.service;

import booster.command.arguments.TrainingSessionMode;
import booster.dao.VocabularyEntryDao;
import booster.dao.params.AddTagToVocabularyEntryDaoParams;
import booster.dao.params.AddVocabularyEntryDaoParams;
import booster.dao.params.UpdateVocabularyEntryDaoParams;
import booster.model.VocabularyEntry;
import booster.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
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
    private final TimeUtil timeUtil;

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
        return switch (mode) {
            case SYNONYMS -> vocabularyEntryDao.countWithSynonyms() > 0;
            case ANTONYMS -> vocabularyEntryDao.countWithAntonyms() > 0;
            default -> false;
        };
    }

    public void delete(long id) {
        transactionTemplate.executeWithoutResult(result -> {
            vocabularyEntryDao.deleteSynonyms(id);
            vocabularyEntryDao.deleteAntonyms(id);
            vocabularyEntryDao.deleteContexts(id);
            vocabularyEntryDao.removeTagAssociationsById(id);
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
        sessionTrackerService.incVocabularyEntriesCount(params.getAddCause());
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

    public void incCorrectAnswersCount(VocabularyEntry entry) {
        updateCorrectAnswersCount(entry, true);
    }

    public void decCorrectAnswersCount(VocabularyEntry entry) {
        updateCorrectAnswersCount(entry, false);
    }

    private void updateCorrectAnswersCount(VocabularyEntry entry, boolean correct) {
        int change = correct ? 1 : -1;
        int newValue = entry.getCorrectAnswersCount() + change;
        if (isValidCorrectAnswersCount(newValue)) {
            vocabularyEntryDao.updateCorrectAnswersCount(entry.getId(), newValue);
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

    @Deprecated
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
            return findById(params.getId()).get();
        });
    }

    // todo: use range OR veIds for synonyms, antonyms, contexts, tags to improve performance
    public List<VocabularyEntry> findAllLimit(Integer limit) {
        List<VocabularyEntry> entries = vocabularyEntryDao.findAllLimit(limit);

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
    public List<VocabularyEntry> findWithSubstringLimit(String substring, Integer limit) {
        List<VocabularyEntry> entries = vocabularyEntryDao.findWithSubstringLimit(substring, limit);

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

    public void updateLastSeenAtById(long id) {
        updateLastSeenAtById(id, timeUtil.timestampNow());
    }

    private void updateLastSeenAtById(long id, Timestamp lastSeenAt) {
        vocabularyEntryDao.updateLastSeenAtById(id, lastSeenAt);
    }

    public void updateLastSeenAtByIds(List<Long> ids) {
        updateLastSeenAtByIds(ids, timeUtil.timestampNow());
    }

    private void updateLastSeenAtByIds(List<Long> ids, Timestamp lastSeenAt) {
        vocabularyEntryDao.updateLastSeenAtByIds(ids, lastSeenAt);
    }

}
