package net.yslibrary.monotweety.login.domain

import com.twitter.sdk.android.core.SessionManager
import com.twitter.sdk.android.core.TwitterSession
import io.reactivex.Completable
import io.reactivex.Single
import net.yslibrary.monotweety.base.di.UserScope
import net.yslibrary.monotweety.data.setting.SettingDataManager
import net.yslibrary.monotweety.data.status.StatusRepository
import net.yslibrary.monotweety.data.user.UserRepository
import javax.inject.Inject


/**
 * Clear all user data
 * this usecase does not remove UserComponent, and does not stop NotificationService.
 * You should manually remove/stop these after this usecase completes.
 * And then you need to navigate to SplashController or finish the Application.
 *
 * Created by yshrsmz on 2016/10/09.
 */
@UserScope
class DoLogout @Inject constructor(private val settingDataManager: SettingDataManager,
                                   private val statusRepository: StatusRepository,
                                   private val userRepository: UserRepository,
                                   private val sessionManager: SessionManager<TwitterSession>) {

  fun execute(): Completable {
    return Single.fromCallable { sessionManager.activeSession?.id }
        .flatMapCompletable { userRepository.delete(it) }
        .andThen(statusRepository.clear())
        .andThen(settingDataManager.clear())
        .andThen(Completable.fromAction { sessionManager.clearActiveSession() })
  }
}