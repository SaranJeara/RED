import unittest
import sys
import os

from robot_session_server import get_robot_version
from robot_session_server import create_libdoc
from robot_session_server import get_classes_from_module


class RobotSessionServerTests(unittest.TestCase):
    def test_encode_result(self):
        response = get_robot_version()

        self.assertTrue(response['result'].startswith('Robot Framework '))
        self.assertEqual(response['exception'], None)

    def test_encode_exception(self):
        response = create_libdoc()

        self.assertEqual(response['result'], None)
        self.assertTrue('TypeError: ' in response['exception'])


class LibdocGenerationTests(unittest.TestCase):
    def test_subsequent_lidocs_for_same_name_libs_under_different_paths_returns_different_libdocs(self):
        parent_path = os.path.dirname(os.path.realpath(__file__))

        response1 = create_libdoc("lib", [os.path.join(parent_path, 'res_test_robot_session_server', 'a')], [])
        response2 = create_libdoc("lib", [os.path.join(parent_path, 'res_test_robot_session_server', 'b')], [])

        self.assertNotEqual(response1, response2)


class ClassesRetrievingTests(unittest.TestCase):
    def test_if_sys_path_is_not_extended_after_retrieving_classes_from_python_module(self):
        parent_path = os.path.dirname(os.path.realpath(__file__))
        module_location = os.path.join(parent_path, 'res_test_robot_session_server', 'classes', 'module', '__init__.py')
        python_paths = [os.path.join(parent_path, 'res_test_robot_session_server', 'a')]
        class_paths = [os.path.join(parent_path, 'res_test_robot_session_server', 'b')]
        old_sys_path = sorted(sys.path)

        get_classes_from_module(module_location, None, python_paths, class_paths)

        self.assertListEqual(old_sys_path, sorted(sys.path))
