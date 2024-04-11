package ait.cohort34.accounting.service;

import ait.cohort34.accounting.dao.UserAccountRepository;
import ait.cohort34.accounting.dto.RolesDto;
import ait.cohort34.accounting.dto.UserDto;
import ait.cohort34.accounting.dto.UserEditDto;
import ait.cohort34.accounting.dto.UserRegisterDto;
import ait.cohort34.accounting.dto.exceptions.IncorrectRoleException;
import ait.cohort34.accounting.dto.exceptions.UserExistsException;
import ait.cohort34.accounting.dto.exceptions.UserNotFoundException;
import ait.cohort34.accounting.model.UserAccount;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDto register(UserRegisterDto userRegisterDto) {
//        if (userAccountRepository.existsById(userRegisterDto.getLogin())) {
//            return null;
//        }
        // OR:

        if (userAccountRepository.existsById(userRegisterDto.getLogin())) {
            throw new UserExistsException();
        }

        UserAccount userAccount = modelMapper.map(userRegisterDto, UserAccount.class);

        String password = BCrypt.hashpw(userRegisterDto.getPassword(), BCrypt.gensalt());
        userAccount.setPassword(password);

        userAccountRepository.save(userAccount);

        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto getUser(String login) {
        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto removeUser(String login) {
        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);
        userAccountRepository.delete(userAccount);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto updateUser(String login, UserEditDto userEditDto) {
        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);

        String firstName = userEditDto.getFirstName();
        if (firstName != null) {
            userAccount.setFirstName(firstName);
        }

        String lastName = userEditDto.getLastName();
        if (lastName != null) {
            userAccount.setLastName(lastName);
        }

        userAccountRepository.save(userAccount);

        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public RolesDto changeRolesList(String login, String role, boolean isAddRole) {
        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);

        role = role.toUpperCase();
        boolean res;

        try {
            if (isAddRole) {
                res = userAccount.addRole(role);
            } else {
                res = userAccount.removeRole(role);
            }
        } catch (Exception e) {
            throw new IncorrectRoleException();
        }

        if (res) {
            userAccountRepository.save(userAccount);
        }

        return modelMapper.map(userAccount, RolesDto.class);
    }

    @Override
    public void changePassword(String login, String newPassword) {
        UserAccount userAccount = userAccountRepository.findById(login).orElseThrow(UserNotFoundException::new);

        String password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        userAccount.setPassword(password);

        userAccountRepository.save(userAccount);
    }
}
