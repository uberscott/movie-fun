package org.superbiz.moviefun.albums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TaskScheduleDAO
{
    private JdbcTemplate template;
    private Logger logger = LoggerFactory.getLogger(TaskScheduleDAO.class);

    public TaskScheduleDAO(JdbcTemplate template)
    {
        this.template = template;
    }

    public Optional<Integer> secureLock(String taskName) {
        int lock_token = Math.abs((int) ((double) Integer.MAX_VALUE * Math.random()));
        int records = template.update("update task_lock set state=1, last_change=CURRENT_TIME(), lock_token=? where name=? and state=0", lock_token,taskName);
        if (records == 1) {
            return Optional.of(lock_token);
        } else if (records > 1) {
            logger.error( "cannot have more than one task with name {}", taskName);
            return Optional.empty();
        }
        else
        {
            return Optional.empty();
        }

    }

    public void releaseLock( String taskName, int lockToken )
    {
        template.update("update task_lock set state=0, last_change=CURRENT_TIME(), lock_token=0 where name=? and lock_token=?",  taskName, lockToken);
    }




}
