package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description:
 */
@Service
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 新增地址
     * @param addressBook
     */
    @Override
    public void add(AddressBook addressBook) {
        log.info("添加地址：{}", addressBook);
        addressBookMapper.insert(addressBook);
    }

    /**
     * 查询地址列表
     * @return
     */
    @Override
    public List<AddressBook> list(AddressBook addressBook) {
        log.info("查询地址列表");
        List<AddressBook> list = addressBookMapper.list(addressBook);
        return list;
    }

    /**4
     * 设置默认地址
     * @param addressBook
     */
    @Override
    public void setDefault(AddressBook addressBook) {
        log.info("设置默认地址：{}", addressBook);
        // 将所有地址的默认地址改为0
        addressBookMapper.updateByUserIdForAll(BaseContext.getCurrentId());
        // 将当前地址的默认地址改为1
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);
    }

    /**
     * 修改地址
     * @param addressBook
     */
    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    public AddressBook getById(Long id) {
        AddressBook addressBook = addressBookMapper.getById(id);
        return addressBook;
    }

    /**
     * 根据id删除地址
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }

    @Override
    public AddressBook getDefault(Long currentId) {
        AddressBook addressBook = addressBookMapper.getDefault(currentId);
        return addressBook;
    }
}
