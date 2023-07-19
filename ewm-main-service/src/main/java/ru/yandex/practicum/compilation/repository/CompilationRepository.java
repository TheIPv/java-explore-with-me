package ru.yandex.practicum.compilation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.compilation.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    @Query("    SELECT c from compilations c "
            + " WHERE c.pinned = :pinned OR :pinned IS NULL")
    List<Compilation> findAllByFilter(Boolean pinned, Pageable pageable);

}
