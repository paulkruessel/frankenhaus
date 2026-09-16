package de.franconia.tuebingen.adh.corps;

import de.franconia.tuebingen.adh.corps.dto.CreateCorpsRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorpsServiceTest {

    @Mock
    CorpsRepository repository;

    @Test
    void createTrimsNameAndReturnsStableId() {
        UUID id = UUID.randomUUID();
        when(repository.existsByName("Franconia")).thenReturn(false);
        when(repository.save(any(Corps.class))).thenAnswer(invocation -> {
            Corps corps = invocation.getArgument(0);
            corps.setId(id);
            return corps;
        });

        CorpsService service = new CorpsService(repository);
        var response = service.createCorps(new CreateCorpsRequest(" Franconia "));

        assertEquals(id, response.id());
        assertEquals("Franconia", response.name());
    }

    @Test
    void createRejectsDuplicateName() {
        when(repository.existsByName("Franconia")).thenReturn(true);

        assertThrows(CorpsAlreadyExistsException.class,
                () -> new CorpsService(repository).createCorps(new CreateCorpsRequest("Franconia")));
    }
}