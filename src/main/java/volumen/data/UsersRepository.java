package volumen.data;

import org.springframework.data.repository.CrudRepository;

import volumen.User;

public interface UsersRepository extends CrudRepository<User, Long> {

}
