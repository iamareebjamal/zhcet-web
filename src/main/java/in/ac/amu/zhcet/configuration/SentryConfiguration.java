package in.ac.amu.zhcet.configuration;

import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.service.UserService;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.event.helper.ContextBuilderHelper;
import io.sentry.event.helper.EventBuilderHelper;
import io.sentry.event.helper.ForwardedAddressResolver;
import io.sentry.event.helper.HttpEventBuilderHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SentryConfiguration {

    @Data
    private static class User {
        private String userId;
        private String name;
        private String departmentName;
        private String email;
        private String[] roles;
        private String type;
    }

    @Autowired
    public SentryConfiguration(UserService userService, ModelMapper modelMapper) {
        EventBuilderHelper myEventBuilderHelper = eventBuilder -> {
            UserAuth loggedInUser = userService.getLoggedInUser();

            if (loggedInUser != null) {
                eventBuilder.withExtra("user", modelMapper.map(loggedInUser, User.class));
            } else {
                eventBuilder.withExtra("user", "UNAUTHORIZED");
            }
        };

        SentryClient sentryClient = Sentry.getStoredClient();
        sentryClient.addBuilderHelper(myEventBuilderHelper);
        sentryClient.addBuilderHelper(new HttpEventBuilderHelper(new ForwardedAddressResolver()));
        sentryClient.addBuilderHelper(new ContextBuilderHelper(sentryClient));
    }

}
