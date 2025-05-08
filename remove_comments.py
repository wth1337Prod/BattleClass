#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import re
import sys

def remove_comments(file_path):
    """Удаляет все однострочные комментарии из Java-файла"""
    try:
        # Чтение файла
        with open(file_path, 'r', encoding='utf-8') as file:
            content = file.read()
        
        # Регулярное выражение для удаления однострочных комментариев
        # Обрабатывает случаи: 
        # 1. // комментарий в конце строки
        # 2. // комментарий на отдельной строке
        # Но сохраняет строки внутри строковых литералов
        pattern = r'(?<!:)\/\/.*?$'
        
        # Удаление комментариев с учетом многострочных выражений
        lines = content.split('\n')
        processed_lines = []
        
        for line in lines:
            # Проверка на строку, состоящую только из комментария
            if line.strip().startswith('//'):
                continue
            
            # Удаление комментариев в конце строки
            processed_line = re.sub(pattern, '', line, flags=re.MULTILINE)
            processed_lines.append(processed_line)
        
        # Сборка обработанного содержимого
        processed_content = '\n'.join(processed_lines)
        
        # Запись обработанного содержимого обратно в файл
        with open(file_path, 'w', encoding='utf-8') as file:
            file.write(processed_content)
        
        return True
    
    except Exception as e:
        print(f"Ошибка при обработке файла {file_path}: {str(e)}")
        return False

def process_directory(directory_path):
    """Обрабатывает все Java-файлы в указанной директории и её поддиректориях"""
    java_files = []
    processed_count = 0
    error_count = 0
    
    # Получение списка всех Java-файлов
    for root, _, files in os.walk(directory_path):
        for file in files:
            if file.endswith('.java'):
                java_files.append(os.path.join(root, file))
    
    total_files = len(java_files)
    print(f"Найдено {total_files} Java-файлов")
    
    # Обработка каждого файла
    for file_path in java_files:
        print(f"Обработка файла: {file_path}")
        if remove_comments(file_path):
            processed_count += 1
        else:
            error_count += 1
    
    print(f"\nОбработка завершена!")
    print(f"Успешно обработано: {processed_count} файлов")
    print(f"Ошибок: {error_count}")

if __name__ == "__main__":
    if len(sys.argv) > 1:
        # Использование пути из аргументов командной строки
        path = sys.argv[1]
    else:
        # Использование текущей директории
        path = os.getcwd()
    
    if not os.path.exists(path):
        print(f"Путь {path} не существует")
        sys.exit(1)
    
    print(f"Начинаю удаление комментариев из Java-файлов в {path}")
    process_directory(path) 