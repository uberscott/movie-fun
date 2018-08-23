package org.superbiz.moviefun.albums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.util.Optional;

@Configuration
@EnableAsync
@EnableScheduling
public class AlbumsUpdateScheduler {

    private static final long SECONDS = 1000;
    private static final long MINUTES = 60 * SECONDS;

    private final AlbumsUpdater albumsUpdater;
    private final TaskScheduleDAO taskScheduleDAO;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Optional<Integer> lockTokenOp = Optional.empty();

    public AlbumsUpdateScheduler(AlbumsUpdater albumsUpdater, TaskScheduleDAO taskScheduleDAO) {
        this.albumsUpdater = albumsUpdater;
        this.taskScheduleDAO = taskScheduleDAO;
    }


    @Scheduled(initialDelay = 15 * SECONDS, fixedRate = 2 * MINUTES)
    public void run() {
        try {
                if (secureLock()) {
                    logger.debug("Starting albums update");
                    albumsUpdater.update();
                    logger.debug("Finished albums update");
                } else {
                    logger.debug("albumTask secure lock rejected.");
                }

        } catch (Throwable e) {
            logger.error("Error while updating albums", e);
        }
        finally

    {
        releaseLock();
    }

    }

    private boolean secureLock()
    {
        try {
            if (lockTokenOp.isPresent()) {
                return false;
            }

            lockTokenOp = taskScheduleDAO.secureLock("album");

            return lockTokenOp.isPresent();
        }
        catch( Throwable t )
        {
            logger.error("Error when attempting to secure task lock", t);
            return false;
        }
    }

    private void releaseLock()
    {
        try
        {
            if( lockTokenOp.isPresent() ) {
                taskScheduleDAO.releaseLock("album", lockTokenOp.get());
            }
        }
        catch( Throwable t )
        {
            logger.error( "erroror while tyring to relase lock", t);
        }
        finally
        {
            lockTokenOp = Optional.empty();
        }
    }
}
