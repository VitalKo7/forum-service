package ait.cohort34.accounting.service;

import ait.cohort34.accounting.dao.UserRepository;
import ait.cohort34.accounting.dto.RolesDto;
import ait.cohort34.accounting.dto.UserDto;
import ait.cohort34.accounting.dto.UserEditDto;
import ait.cohort34.accounting.dto.UserRegisterDto;
import ait.cohort34.accounting.dto.exceptions.UserNotFoundException;
import ait.cohort34.accounting.model.UserAccount;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDto register(UserRegisterDto userRegisterDto) {

        if (userRepository.existsById(userRegisterDto.getLogin())) {
            return null;
        }

        UserAccount userAccount = modelMapper.map(userRegisterDto, UserAccount.class);
        userRepository.save(userAccount);

        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto getUser(String login) {
        UserAccount userAccount = userRepository.findById(login).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto removeUser(String login) {
        UserAccount userAccount = userRepository.findById(login).orElseThrow(UserNotFoundException::new);
        userRepository.delete(userAccount);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto updateUser(String login, UserEditDto userEditDto) {
        UserAccount userAccount = userRepository.findById(login).orElseThrow(UserNotFoundException::new);

        String firstName = userEditDto.getFirstName();
        if (firstName != null) {
            userAccount.setFirstName(firstName);
        }

        String lastName = userEditDto.getLastName();
        if (lastName != null) {
            userAccount.setLastName(lastName);
        }

        userRepository.save(userAccount);

        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public RolesDto changeRolesList(String login, String role, boolean isAddRole) {
        UserAccount userAccount = userRepository.findById(login).orElseThrow(UserNotFoundException::new);

        if (isAddRole) {
            // FIXME: check if the role available
            userAccount.addRole(role);
        } else {
            // FIXME: check if the role available
            userAccount.removeRole(role);
        }

        return modelMapper.map(userAccount, RolesDto.class);
    }

    @Override
    public void changePassword(String login, String newPassword) {

    }
}
