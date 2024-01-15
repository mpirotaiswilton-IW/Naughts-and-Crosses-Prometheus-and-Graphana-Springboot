package com.max_pw_iw.naughtsandcrosses.meter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.max_pw_iw.naughtsandcrosses.entity.User;
import com.max_pw_iw.naughtsandcrosses.service.UserService;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;

// import java.util.Collections;
// import java.util.List;
import java.util.function.Supplier;

@Component
@Getter
public class UserMetricsBean {

    @Lazy
    @Autowired
    protected UserService userService;

    private Counter userHistoricalCounter;

    public Supplier<Number> fetchUserCount() {
        return ()->userService.getAllUsers().size();
    }

    /*TODO:

        This doesn't do what it says it does,
        Make it do what it should do:

        What it does now:   Fetches the highest id value from the user list,
                            Assuming it is the sum over every user that is 
                            and that has been (which it isn't)

        What it SHOULD do:  Get a historical cumulative count of users
                            that have ever been registered 
    */
    // public Supplier<Number> fetchUserCumulativeCount() {
    //     return ()->{
    //         List<User> userList = userService.getAllUsers();
            
    //         Collections.sort(userList, new UserComparator());

    //         User[] usersArray = userList.toArray(new User[userList.size()]);

    //         return usersArray[0].getId();
    //     };
    // }

    public UserMetricsBean(MeterRegistry registry){
        Gauge.builder("number.of.users",fetchUserCount())
        //.tag("type","current")        
        .register(registry);
        userHistoricalCounter = Counter.builder("historical.number.of.users")
        //.tag("type","cumulative-historical")        
        .register(registry);
    }

}

// class UserComparator implements java.util.Comparator<User> {
//     @Override
//     public int compare(User a, User b) {
//         return (int) (b.getId() - a.getId());
//     }
// }
